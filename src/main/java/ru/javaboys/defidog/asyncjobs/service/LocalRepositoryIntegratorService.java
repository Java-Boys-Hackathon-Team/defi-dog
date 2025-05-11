package ru.javaboys.defidog.asyncjobs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.asyncjobs.util.SourceStorageService;
import ru.javaboys.defidog.entity.SourceCode;

import java.io.File;
import java.nio.file.Path;
import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalRepositoryIntegratorService {

    private final GitRepositoryService gitRepositoryService;
    private final SourceStorageService storageService;
    private final ChangeSetService changeSetService;

    public String integrateAndProcess(SourceCode sourceCode, Path repoPath) throws Exception {
        StringBuilder jobLog = new StringBuilder();
        File repoDir = repoPath.toFile();

        Git git = new Git(new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build());

        // Получение HEAD commit SHA
        ObjectId headId = git.getRepository().resolve("HEAD");
        if (headId == null) {
            jobLog.append("Не удалось получить HEAD commit SHA.\n");
            return jobLog.toString();
        }

        String aggregatedCode = gitRepositoryService.getFullSourceCode(repoDir);
        sourceCode.setLastKnownSourceCode(aggregatedCode);

        var abiCandidates = gitRepositoryService.extractAbiInfo(repoDir);
        if (!abiCandidates.isEmpty()) {
            var abi = abiCandidates.get(0);
            sourceCode.setLastKnownAbi(abi.json());
            sourceCode.setAbiFilePath(abi.path());
            jobLog.append("Найден ABI-файл: ").append(abi.path()).append("\n");
        } else {
            jobLog.append("ABI не найден.\n");
        }

        changeSetService.createChangeSetsIfNeeded(sourceCode, repoDir, headId.getName());

        String commitSha = headId.getName();
        sourceCode.setLastCommitSha(commitSha);
        jobLog.append("Последний коммит: ").append(commitSha).append("\n");

        sourceCode.setFetchedAt(OffsetDateTime.now());
        sourceCode.setLocalPath(storageService.getRelativePath(repoPath));

        return jobLog.toString();
    }
}

