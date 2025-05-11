package ru.javaboys.defidog.integrations.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.javaboys.defidog.entity.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {

    private final TelegramClient telegramClient;

    @SneakyThrows
    @Override
    public String getBotName() {
        return telegramClient.execute(new GetMe()).getUserName();
    }

    @Override
    @SneakyThrows
    public void sendMessageToUser(String message, User user) {

        SendMessage msg = SendMessage
                .builder()
                .chatId(user.getTelegramUser().getTelegramChatId())
                .text(message)
                .build();

        telegramClient.execute(msg);
    }

    @SneakyThrows
    @Override
    public void sendAuditReportToUser(String header, String message, User user) {

        SendMessage msg = SendMessage
                .builder()
                .chatId(user.getTelegramUser().getTelegramChatId())
                .parseMode("HTML")
                .text(String.format("<b>%s</b>\n%s", header, message))
                .build();
        telegramClient.execute(msg);
    }

}
