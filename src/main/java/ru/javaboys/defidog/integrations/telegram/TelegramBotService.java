package ru.javaboys.defidog.integrations.telegram;

import ru.javaboys.defidog.entity.User;

public interface TelegramBotService {
    void sendMessageToUser(String message, User user);
}
