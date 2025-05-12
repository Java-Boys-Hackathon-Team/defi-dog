package ru.javaboys.defidog.asyncjobs.service;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeSetInitializationService {

    private final UnconstrainedDataManager dataManager;
    private final GitRepositoryService gitRepositoryService;
    private final ChangeSummaryGenerationService changeSummaryGenerationService;

    public void createInitialChangeSetsIfMissing() {
        log.info("🔍 Поиск SourceCode без SourceCodeChangeSet...");

        List<SourceCode> withoutChangeSets = dataManager.load(SourceCode.class)
                .query("select sc from SourceCode sc where not exists " +
                        "(select cs from SourceCodeChangeSet cs where cs.sourceCode = sc)")
                .list();

        for (SourceCode sourceCode : withoutChangeSets) {
            try {
                log.info("📥 Инициализация SourceCode ID={}", sourceCode.getId());

                File repoDir = Path.of(sourceCode.getLocalPath()).toFile();
                Git git = gitRepositoryService.openGit(repoDir);
                ObjectId headId = git.getRepository().resolve("HEAD");
                if (headId == null) {
                    log.warn("⚠️ HEAD не найден у репозитория {}", sourceCode.getId());
                    continue;
                }

                Repository repo = git.getRepository();
                RevCommit headCommit = new RevWalk(repo).parseCommit(headId);
                String commitSha = headCommit.getName();

                // --- Инициализация SourceCodeChangeSet
                String fullDiff = sourceCode.getLastKnownSourceCode();
                SourceCodeChangeSet scChangeSet = dataManager.create(SourceCodeChangeSet.class);
                scChangeSet.setSourceCode(sourceCode);
                scChangeSet.setCommitHash(commitSha);
                scChangeSet.setGitDiff(fullDiff);
                scChangeSet.setChangeSummary(changeSummaryGenerationService.generateInitialSourceCodeSummary(scChangeSet));
                dataManager.save(scChangeSet);

                // --- Инициализация AbiChangeSet (если есть ABI)
                if (sourceCode.getLastKnownAbi() != null) {
                    String abiContent = sourceCode.getLastKnownAbi();
                    AbiChangeSet abiChangeSet = dataManager.create(AbiChangeSet.class);
                    abiChangeSet.setSourceCode(sourceCode);
                    abiChangeSet.setCommitHash(commitSha);
                    abiChangeSet.setGitDiff(abiContent);
                    abiChangeSet.setChangeSummary(changeSummaryGenerationService.generateInitialAbiSummary(abiChangeSet));
                    dataManager.save(abiChangeSet);
                } else {
                    log.info("🔍 У SourceCode {} не указан abiFilePath, AbiChangeSet не создаётся", sourceCode.getId());
                }

                log.info("✅ Инициализация завершена для SourceCode {}", sourceCode.getId());

                // --- Автоматическая привязка ScanTool-ов
                if (sourceCode.getScanTools() == null || sourceCode.getScanTools().isEmpty()) {
                    List<ScanTool> activeTools = dataManager.load(ScanTool.class)
                            .query("select t from ScanTool t where t.active = true")
                            .list();

                    sourceCode.setScanTools(activeTools);
                    dataManager.save(sourceCode);

                    log.info("🔗 Привязаны {} ScanTool-ов к SourceCode ID={}", activeTools.size(), sourceCode.getId());
                }


            } catch (Exception e) {
                log.error("Ошибка при инициализации ChangeSet для SourceCode {}: {}", e.getMessage(), sourceCode.getId(), e);
            }
        }
    }
}
