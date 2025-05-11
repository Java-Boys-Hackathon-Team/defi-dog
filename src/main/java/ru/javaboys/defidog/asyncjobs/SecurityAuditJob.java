package ru.javaboys.defidog.asyncjobs;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.javaboys.defidog.asyncjobs.service.AuditReportService;
import ru.javaboys.defidog.asyncjobs.service.SecurityScannerService;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.Notification;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;
import ru.javaboys.defidog.entity.User;

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

            AuditReport report = auditReportService.generateReport(changeSet);
            List<Notification> notifications = buildNotifications(report);
            if (CollectionUtils.isNotEmpty(notifications)) {
                dataManager.saveAll(notifications);
            }
        }

        log.info("Джоба аудита безопасности завершена");
    }

    private List<Notification> buildNotifications(AuditReport report) {
        SmartContract smartContract = report.getSmartContract();
        if (smartContract == null) {
            return List.of();
        }

        List<User> users = null;
        if (smartContract.getCryptocurrency() != null) {
            users = dataManager.load(User.class)
                    .query("select distinct n.user from NotificationSettings n " +
                           "where :currency in(n.subscribedCryptocurrencies)")
                    .parameter("currency", smartContract.getCryptocurrency())
                    .list();
        } else if (smartContract.getDeFiProtocol() == null) {
            users = dataManager.load(User.class)
                    .query("select distinct n.user from NotificationSettings n " +
                           "where :protocol in(n.subscribedDeFiProtocols)")
                    .parameter("protocol", smartContract.getDeFiProtocol())
                    .list();
        }

        if (CollectionUtils.isEmpty(users)) {
            return List.of();
        }

        return users.stream()
                .map(u -> {
                    Notification n = dataManager.create(Notification.class);
                    n.setUser(u);
                    n.setHeader(report.getDescription());
                    n.setMessage(report.getSummary());
                    if (StringUtils.isNotBlank(u.getEmail())) {
                        n.setEmailSent(false);
                    }
                    if (u.getTelegramUser() != null) {
                        n.setTelegramSent(false);
                    }
                    return n;
                })
                .filter(n -> n.getEmailSent() != null || n.getTelegramSent() != null)
                .toList();
    }

}
