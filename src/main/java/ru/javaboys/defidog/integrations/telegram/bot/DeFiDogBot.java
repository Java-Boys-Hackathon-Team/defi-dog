package ru.javaboys.defidog.integrations.telegram.bot;

import io.jmix.core.security.SystemAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeFiDogBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    @Value("${telegram.bot.token}")
    private String token;

    private final TelegramClient telegramClient;

    private final SystemAuthenticator systemAuthenticator;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @SneakyThrows
    @Override
    @Transactional
    public void consume(Update update) {

        systemAuthenticator.begin("admin");

        try {

            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                SendMessage message = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(messageText)
                        .build();

                telegramClient.execute(message);
            }

        } finally {
            systemAuthenticator.end();
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered DeFi Dog Bot running state is: {}", botSession.isRunning());
    }
}