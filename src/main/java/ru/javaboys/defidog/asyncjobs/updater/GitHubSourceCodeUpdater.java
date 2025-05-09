package ru.javaboys.defidog.asyncjobs.updater;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.asyncjobs.service.GitRepositoryService;
import ru.javaboys.defidog.asyncjobs.util.SourceStorageService;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;

import java.io.File;
import java.nio.file.Path;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubSourceCodeUpdater implements SourceCodeUpdater, TypedUpdater {

    private final SourceStorageService storageService;

    private final GitRepositoryService gitRepositoryService;

    @Override
    public SourceType getSupportedSourceType() {
        return SourceType.GITHUB;
    }

    @Override
    public String update(SourceCode sourceCode) throws Exception {
        StringBuilder jobLog = new StringBuilder();
        Path localPath = storageService.getSourceDirectory(sourceCode);
        File repoDir = localPath.toFile();
        String branch = (sourceCode.getBranch() != null) ? sourceCode.getBranch() : "main";
        String repoUrl = sourceCode.getRepoUrl();

        jobLog.append("Источник: GITHUB\n")
                .append("URL: ").append(repoUrl).append("\n")
                .append("Ветка: ").append(branch).append("\n");

        Git git;
        if (!repoDir.exists() || !new File(repoDir, ".git").exists()) {
            jobLog.append("Клонирование репозитория...\n");
            git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setBranch(branch)
                    .setDirectory(repoDir)
                    .call();
            jobLog.append("Клонирование завершено.\n");
        } else {
            jobLog.append("Локальный репозиторий найден. Обновление...\n");
            Repository repo = new FileRepositoryBuilder()
                    .setGitDir(new File(repoDir, ".git"))
                    .build();
            git = new Git(repo);

            PullResult result = git.pull()
                    .setRemoteBranchName(branch)
                    .call();

            if (result.isSuccessful()) {
                jobLog.append("Pull выполнен успешно.\n");
            } else {
                jobLog.append("Pull завершился с ошибкой.\n");
            }
        }

        // Получение HEAD commit SHA
        ObjectId headId = git.getRepository().resolve("HEAD");
        if (headId != null) {
            String commitSha = headId.getName();
            sourceCode.setLastCommitSha(commitSha);
            jobLog.append("Последний коммит: ").append(commitSha).append("\n");
        }

        String aggregatedCode = gitRepositoryService.getFullSourceCode(repoDir);
        sourceCode.setLastKnownSourceCode(aggregatedCode);

        sourceCode.setFetchedAt(OffsetDateTime.now());
        sourceCode.setLocalPath(storageService.getRelativePath(localPath));

        return jobLog.toString();
    }
}
