package ru.javaboys.defidog.asyncjobs.service;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.Notification;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UnconstrainedDataManager dataManager;

    public List<Notification> buildNotifications(AuditReport report) {
        log.info("Starting to build notifications for audit report: {}", report.getId());
        SmartContract smartContract = report.getSmartContract();
        if (smartContract == null) {
            log.info("No smart contract found for audit report: {}, skipping notification creation", report.getId());
            return List.of();
        }

        List<User> users = null;
        if (smartContract.getCryptocurrency() != null) {
            log.info("Finding users subscribed to cryptocurrency: {}", smartContract.getCryptocurrency().getName());
            users = dataManager.load(User.class)
                    .query("select distinct n.user from NotificationSettings n " +
                            "where :currency in(n.subscribedCryptocurrencies)")
                    .parameter("currency", smartContract.getCryptocurrency())
                    .list();
        } else if (smartContract.getDeFiProtocol() != null) {
            log.info("Finding users subscribed to DeFi protocol: {}", smartContract.getDeFiProtocol().getName());
            users = dataManager.load(User.class)
                    .query("select distinct n.user from NotificationSettings n " +
                            "where :protocol in(n.subscribedDeFiProtocols)")
                    .parameter("protocol", smartContract.getDeFiProtocol())
                    .list();
        } else {
            log.info("Smart contract has neither cryptocurrency nor DeFi protocol assigned");
        }

        if (CollectionUtils.isEmpty(users)) {
            log.info("No users found for notification about audit report: {}", report.getId());
            return List.of();
        }

        log.info("Found {} users to notify about audit report: {}", users.size(), report.getId());
        List<Notification> notifications = users.stream()
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

        log.info("Created {} notifications for audit report: {}", notifications.size(), report.getId());
        return notifications;
    }
}
