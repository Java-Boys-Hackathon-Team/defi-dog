package ru.javaboys.defidog.asyncjobs.service;


import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeSetService {

    private final GitRepositoryService gitRepositoryService;
    private final UnconstrainedDataManager dataManager;
    private final ChangeSummaryGenerationService changeSummaryGenerationService;

    public void createChangeSetsIfNeeded(SourceCode sourceCode, File repoDir, String newHeadCommit) {
        try (Git git = gitRepositoryService.openGit(repoDir)) {
            String oldCommit = sourceCode.getLastCommitSha();
            if (oldCommit == null || oldCommit.equals(newHeadCommit)) {
                log.info("Коммит не изменился, change set не требуется для {}", sourceCode.getId());
                return;
            }

            Repository repo = git.getRepository();
            ObjectId oldId = repo.resolve(oldCommit);
            ObjectId newId = repo.resolve(newHeadCommit);

            // --- Source code change set (по .sol, .vy и т.п.)
            String sourceCodeDiff = getGitDiff(repo, oldId, newId, path -> {
                String p = path.toLowerCase();
                return p.endsWith(".sol") || p.endsWith(".vy");
            });

            if (!sourceCodeDiff.isBlank()) {
                SourceCodeChangeSet codeChangeSet = dataManager.create(SourceCodeChangeSet.class);
                codeChangeSet.setSourceCode(sourceCode);
                codeChangeSet.setCommitHash(newHeadCommit);
                codeChangeSet.setGitDiff(sourceCodeDiff);

                String summary = changeSummaryGenerationService.generateSourceCodeSummary(codeChangeSet);
                codeChangeSet.setChangeSummary(summary);

                dataManager.save(codeChangeSet);
            }

            // --- ABI change set (по .json, .abi и т.п.)
            String abiDiff = getGitDiff(repo, oldId, newId, path -> {
                if (sourceCode.getAbiFilePath() != null) {
                    return path.equals(sourceCode.getAbiFilePath());
                } else {
                    String p = path.toLowerCase();
                    return p.endsWith(".json") || p.contains("abi");
                }
            });

            if (!abiDiff.isBlank()) {
                AbiChangeSet abiChangeSet = dataManager.create(AbiChangeSet.class);
                abiChangeSet.setSourceCode(sourceCode);
                abiChangeSet.setCommitHash(newHeadCommit);
                abiChangeSet.setGitDiff(abiDiff);

                String summary = changeSummaryGenerationService.generateAbiSummary(abiChangeSet);
                abiChangeSet.setChangeSummary(summary);

                dataManager.save(abiChangeSet);
            }

        } catch (Exception e) {
            log.error("Ошибка при создании changeSet для {}", sourceCode.getId(), e);
        }
    }

    private String getGitDiff(Repository repo, ObjectId oldId, ObjectId newId, Predicate<String> pathFilter) throws Exception {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DiffFormatter formatter = new DiffFormatter(out);
                RevWalk walk = new RevWalk(repo)
        ) {

            RevCommit oldCommit = walk.parseCommit(oldId);
            RevCommit newCommit = walk.parseCommit(newId);

            try (var reader = repo.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldCommit.getTree());

                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, newCommit.getTree());

                formatter.setRepository(repo);
                formatter.setDetectRenames(true); // на всякий случай

                List<DiffEntry> diffs = formatter.scan(oldTreeIter, newTreeIter)
                        .stream()
                        .filter(diff -> {
                            String newPath = diff.getNewPath();
                            return newPath != null && pathFilter.test(newPath);
                        })
                        .toList();

                for (DiffEntry diff : diffs) {
                    formatter.format(diff);
                }

                return out.toString();
            }
        }
    }
}
