package ru.javaboys.defidog.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import ru.javaboys.defidog.utils.MailTemplates;

@Service
public class MailService {

    @Autowired
    private Emailer emailer;

    public void sendEmailSetupCode(String to, String code) {
        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setAddresses(to)
                .setSubject("Код подтверждения адреса")
                .setFrom(null)
                .setBody(MailTemplates.forCode(code))
                .setImportant(true)
                .setBodyContentType("text/html; charset=UTF-8")
                .build();
        emailer.sendEmailAsync(emailInfo);
    }

    public void sendEmailNotification(String to, String subject, String body) {
        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setAddresses(to)
                .setSubject(subject)
                .setFrom(null)
                .setBody(MailTemplates.forNotification(body))
                .setImportant(true)
                .setBodyContentType("text/html; charset=UTF-8")
                .build();
        emailer.sendEmailAsync(emailInfo);
    }

}
