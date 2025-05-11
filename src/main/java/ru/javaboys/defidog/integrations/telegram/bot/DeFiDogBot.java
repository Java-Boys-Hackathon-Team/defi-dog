package ru.javaboys.defidog.integrations.telegram.bot;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.javaboys.defidog.entity.ChannelEnum;
import ru.javaboys.defidog.entity.CodeEntity;
import ru.javaboys.defidog.entity.TelegramUser;
import ru.javaboys.defidog.entity.User;
import ru.javaboys.defidog.integrations.telegram.TelegramUserService;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram.bot", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DeFiDogBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    @Value("${telegram.bot.token}")
    private String token;

    private final TelegramClient telegramClient;

    private final SystemAuthenticator systemAuthenticator;

    private final DataManager dataManager;

    private final TelegramUserService telegramUserService;

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
            String input = update.getMessage().getText();
            if (input.startsWith("/start")) {
                processSetup(update);
            }

        } finally {
            systemAuthenticator.end();
        }
    }

    private void processSetup(Update update) throws TelegramApiException {

        String input = update.getMessage().getText();
        String code = input.substring("/start".length()).trim();

        Optional<CodeEntity> codeEntityOptional = dataManager.load(CodeEntity.class)
                .query("select c from CodeEntity c where c.type = :type and c.code = :code order by c.createdDate desc")
                .parameter("code", code)
                .parameter("type", ChannelEnum.TELEGRAM.name())
                .optional();

        if (codeEntityOptional.isEmpty()) {
            sendHtmlMessage(update, "Привязка не удалась, попробуйте еще раз");
            return;
        }

        TelegramUser upsertedTelegramUser = telegramUserService.upsertTelegramUser(update);

        CodeEntity codeEntity = codeEntityOptional.get();
        User user = codeEntity.getUser();

        user.setTelegramUser(upsertedTelegramUser);
        dataManager.save(user);

        sendHtmlMessage(update, "<b>Telegram успешно привязан.</b>\nТеперь вы будете получать все важные уведомления на ваш telegram аккаунт");
    }

    private void sendHtmlMessage(Update update, String text) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .parseMode("HTML")
                .text(text)
                .build();
        telegramClient.execute(message);
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered DeFi Dog Bot running state is: {}", botSession.isRunning());
    }
}