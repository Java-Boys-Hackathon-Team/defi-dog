package ru.javaboys.defidog.asyncjobs.updater;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.asyncjobs.service.ChangeSetService;
import ru.javaboys.defidog.asyncjobs.service.GitRepositoryService;
import ru.javaboys.defidog.asyncjobs.util.SourceStorageService;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;

import java.io.File;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubSourceCodeUpdater implements SourceCodeUpdater, TypedUpdater {

    private final SourceStorageService storageService;

    private final GitRepositoryService gitRepositoryService;

    private final ChangeSetService changeSetService;

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
        if (headId == null) {
            jobLog.append("Не удалось получить HEAD commit SHA.\n");
            return jobLog.toString();
        }

        String aggregatedCode = gitRepositoryService.getFullSourceCode(repoDir);
        sourceCode.setLastKnownSourceCode(aggregatedCode);

        List<String> abis = gitRepositoryService.extractAbiStrings(repoDir);
        if (!abis.isEmpty()) {
            // Запишем первый найденный ABI
            sourceCode.setLastKnownAbi(abis.get(0));
            jobLog.append("Найден ABI. Кол-во вариантов: ").append(abis.size()).append("\n");
        } else {
            jobLog.append("ABI не найден в репозитории.\n");
        }

        changeSetService.createChangeSetsIfNeeded(sourceCode, repoDir, headId.getName(), sourceCode.getLastKnownAbi());

        String commitSha = headId.getName();
        sourceCode.setLastCommitSha(commitSha);
        jobLog.append("Последний коммит: ").append(commitSha).append("\n");

        sourceCode.setFetchedAt(OffsetDateTime.now());
        sourceCode.setLocalPath(storageService.getRelativePath(localPath));

        return jobLog.toString();
    }
}
