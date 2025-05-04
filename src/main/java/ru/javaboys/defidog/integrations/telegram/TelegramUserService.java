package ru.javaboys.defidog.integrations.telegram;

import io.jmix.core.DataManager;
import io.jmix.core.querycondition.PropertyCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.javaboys.defidog.entity.TelegramUser;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TelegramUserService {

    private final DataManager dataManager;

    @Transactional
    public TelegramUser findUserByTelegramUserId(Long telegramUserId) {
        return dataManager.load(TelegramUser.class)
                .condition(PropertyCondition.equal("telegramUserId", telegramUserId))
                .one();
    }

    public TelegramUser updateUser(TelegramUser telegramUser) {
        return dataManager.save(telegramUser);
    }

    @Transactional
    public TelegramUser upsertTelegramUser(Update update) {

        TelegramUser upsertedTelegramUser;

        String firstName = "";
        String lastName  = "";
        String userName  = "";

        Long userId = 0L;
        Long chatId = 0L;

        if (update.hasMessage()) {

            firstName = update.getMessage().getChat().getFirstName();
            lastName = update.getMessage().getChat().getLastName();
            userName = update.getMessage().getChat().getUserName();

            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();

        } else if (update.hasCallbackQuery()) {

            firstName = update.getCallbackQuery().getMessage().getChat().getFirstName();
            lastName = update.getCallbackQuery().getMessage().getChat().getLastName();
            userName = update.getCallbackQuery().getMessage().getChat().getUserName();

            userId = update.getCallbackQuery().getMessage().getChat().getId();
            chatId = update.getCallbackQuery().getMessage().getChatId();

        }

        if (userId == 0L || chatId == 0L || Objects.equals(firstName, "") || Objects.equals(lastName, "") || Objects.equals(userName, "")) {
            throw new RuntimeException("Telegram user properties cannot be empty");
        }

        var optionalTelegramUser = dataManager.load(TelegramUser.class)
                .query("e.telegramUserId = ?1", userId)
                .optional();

        TelegramUser tgUser;

        if (optionalTelegramUser.isPresent()) {
            tgUser = optionalTelegramUser.get();
        } else {
            tgUser = dataManager.create(TelegramUser.class);
            tgUser.setTelegramUserId(userId);
        }

        tgUser.setTelegramUserFirstName(firstName);
        tgUser.setTelegramUserLastName(lastName);
        tgUser.setTelegramUserName(userName);
        tgUser.setTelegramChatId(chatId);

        upsertedTelegramUser = dataManager.save(tgUser);

        return upsertedTelegramUser;
    }
}
