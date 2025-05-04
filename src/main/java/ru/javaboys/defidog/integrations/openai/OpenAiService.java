package ru.javaboys.defidog.integrations.openai;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

public interface OpenAiService {
    String talkToChatGPT(String conversationId, SystemMessage systemMessage, UserMessage userMessage);
}
