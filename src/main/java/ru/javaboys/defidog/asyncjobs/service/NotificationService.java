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
        log.info("Начинаем создание уведомлений для аудиторского отчета: {}", report.getId());
        SmartContract smartContract = report.getSmartContract();
        if (smartContract == null) {
            log.info("Не найден смарт-контракт для аудиторского отчета: {}, пропускаем создание уведомлений", report.getId());
            return List.of();
        }

        List<User> users = null;
        if (smartContract.getCryptocurrency() != null) {
            log.info("Поиск пользователей, подписанных на криптовалюту: {}", smartContract.getCryptocurrency().getName());
            users = dataManager.load(User.class)
                    .query("select distinct n.user from NotificationSettings n join n.subscribedCryptocurrencies c where c = :currency")
                    .parameter("currency", smartContract.getCryptocurrency())
                    .list();
        } else if (smartContract.getDeFiProtocol() != null) {
            log.info("Поиск пользователей, подписанных на DeFi протокол: {}", smartContract.getDeFiProtocol().getName());
            users = dataManager.load(User.class)
                    .query("select distinct n.user from NotificationSettings n join n.subscribedDeFiProtocols p where p = :protocol")
                    .parameter("protocol", smartContract.getDeFiProtocol())
                    .list();
        } else {
            log.info("Смарт-контракт не имеет ни криптовалюты, ни DeFi протокола");
        }

        if (CollectionUtils.isEmpty(users)) {
            log.info("Не найдено пользователей для уведомления об аудиторском отчете: {}", report.getId());
            return List.of();
        }

        log.info("Найдено {} пользователей для уведомления об аудиторском отчете: {}", users.size(), report.getId());
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

        log.info("Создано {} уведомлений для аудиторского отчета: {}", notifications.size(), report.getId());
        return notifications;
    }
}
