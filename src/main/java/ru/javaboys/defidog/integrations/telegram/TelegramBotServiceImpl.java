package ru.javaboys.defidog.integrations.telegram;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.javaboys.defidog.entity.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {

    private final TelegramClient telegramClient;

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
}
