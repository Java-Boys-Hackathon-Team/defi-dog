package ru.javaboys.defidog.asyncjobs;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import ru.javaboys.defidog.entity.Notification;
import ru.javaboys.defidog.integrations.telegram.TelegramBotService;
import ru.javaboys.defidog.mail.MailService;

@Component
@RequiredArgsConstructor
public class NotificationJob {
    private final UnconstrainedDataManager dataManager;
    private final MailService mailService;
    private final TelegramBotService telegramBotService;

    @Scheduled(fixedRate = 30 * 1000)
    public void scheduledJob() {
        runEmailJob();
        runTelegramJob();
    }

    public void runEmailJob() {
        List<Notification> notifications = dataManager.load(Notification.class)
                .query("select n from Notification n where n.emailSent = false order by n.createdDate asc")
                .maxResults(10)
                .list();

        for (Notification notification : notifications) {
            mailService.sendEmailNotification(
                    notification.getUser().getEmail(), notification.getHeader(), notification.getMessage()
            );

            notification.setEmailSent(true);
            dataManager.save(notification);
        }

    }

    public void runTelegramJob() {
        List<Notification> notifications = dataManager.load(Notification.class)
                .query("select n from Notification n where n.telegramSent = false order by n.createdDate asc")
                .maxResults(10)
                .list();

        for (Notification notification : notifications) {
            telegramBotService.sendAuditReportToUser(
                    notification.getHeader(), notification.getMessage(), notification.getUser()
            );

            notification.setTelegramSent(true);
            dataManager.save(notification);
        }

    }

}
