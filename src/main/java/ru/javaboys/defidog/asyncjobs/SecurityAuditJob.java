package ru.javaboys.defidog.asyncjobs;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javaboys.defidog.asyncjobs.service.AuditReportService;
import ru.javaboys.defidog.asyncjobs.service.SecurityScannerService;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityAuditJob {

    private final UnconstrainedDataManager dataManager;
    private final SecurityScannerService securityScannerService;
    private final AuditReportService auditReportService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void run() {
        log.info("Запуск джобы аудита безопасности смарт-контрактов");

        List<SourceCodeChangeSet> changeSets = dataManager.load(SourceCodeChangeSet.class)
                .query("select cs from SourceCodeChangeSet cs where cs.securityScanJobResult is empty")
                .list();

        for (SourceCodeChangeSet changeSet : changeSets) {
            SourceCode sourceCode = changeSet.getSourceCode();

            List<ScanTool> tools = dataManager.load(ScanTool.class)
                    .query("select t from ScanTool t where t.active = true and t.sourceCode = :sc")
                    .parameter("sc", sourceCode)
                    .list();

            if (tools.isEmpty()) {
                log.info("Нет активных сканеров для SourceCode ID={}", sourceCode.getId());
                continue;
            }

            for (ScanTool tool : tools) {
                try {
                    log.info("Запуск сканера {} для SourceCodeChangeSet {}", tool.getName(), changeSet.getId());

                    SourceCodeSecurityScanJob job = securityScannerService.runScanTool(tool, changeSet);
                    dataManager.save(job);

                } catch (Exception e) {
                    log.error("Ошибка при запуске сканера {} для changeSet {}: {}", tool.getName(), changeSet.getId(), e.getMessage(), e);
                }
            }

            auditReportService.generateReport(changeSet);
        }

        log.info("Джоба аудита безопасности завершена");
    }
}
