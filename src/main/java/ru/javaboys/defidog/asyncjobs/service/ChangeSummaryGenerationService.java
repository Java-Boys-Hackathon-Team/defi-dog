package ru.javaboys.defidog.asyncjobs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.asyncjobs.util.CommonUtils;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.integrations.openai.OpenAiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeSummaryGenerationService {

    private final OpenAiService openAiService;

    public String generateSourceCodeSummary(SourceCodeChangeSet changeSet) {
        String diff = changeSet.getGitDiff();
        String commitHash = changeSet.getCommitHash();

        SystemMessage systemMessage = new SystemMessage("""
            Ты — эксперт по аудиту безопасности смарт-контрактов и блокчейн-инвестированию.
            Проанализируй изменения в исходном коде на профессиональном уровне.
            Твоя задача — написать краткое, но глубокое описание изменений в коде:
            - Поясни, как эти изменения могут повлиять на поведение контракта.
            - Укажи, повышают ли они риски безопасности или наоборот — устраняют уязвимости.
            - Подчеркни ключевые особенности для крипто-инвесторов: добавлены ли функции, изменена ли логика распределения токенов, и т.п.
            Стиль — деловой, без избыточных технических деталей, но с глубоким пониманием.
        """);

        UserMessage userMessage = new UserMessage("""
            Проанализируй git diff, содержащий изменения смарт-контрактов Ethereum (Solidity).
            Коммит: %s
        
            Вот diff:
            ```
            %s
            ```
        
            Сформируй резюме, понятное одновременно крипто-инвестору и аудитору.
        """.formatted(commitHash, diff));

        return openAiService.talkToChatGPT(changeSet.getId().toString(), systemMessage, userMessage);
    }

    public String generateAbiSummary(AbiChangeSet changeSet) {
        String diff = changeSet.getGitDiff();
        String commitHash = changeSet.getCommitHash();

        SystemMessage systemMessage = new SystemMessage("""
            Ты — эксперт по API-интерфейсам, блокчейн-инвестированию и безопасности Ethereum.
            Проанализируй изменения в ABI (Application Binary Interface) смарт-контракта:
            - Опиши, какие функции были добавлены/удалены/изменены.
            - Влияют ли эти изменения на публичный API, безопасность, доверие к контракту?
            - Есть ли изменения, которые могут повлиять на взаимодействие с фронтендом, кошельками или DEX?
            Объясни последствия для инвесторов и разработчиков.
        """);

        UserMessage userMessage = new UserMessage("""
            Ниже — git diff файла ABI, отражающий изменения публичного интерфейса смарт-контракта.
            Коммит: %s
        
            ```
            %s
            ```

            Напиши краткое, но глубокое описание этих изменений.
        """.formatted(commitHash, diff));

        return openAiService.talkToChatGPT(changeSet.getId().toString(), systemMessage, userMessage);
    }

    public String generateInitialSourceCodeSummary(SourceCodeChangeSet changeSet) {
        String diff = changeSet.getGitDiff();
        diff = CommonUtils.splitSolidityContractsInBatches(diff, 3000).get(0);
        SystemMessage systemMessage = new SystemMessage("""
        Ты — специалист по смарт-контрактам и блокчейн-инфраструктуре.
        Проанализируй текущий исходный код (без изменений), чтобы составить обзор его архитектуры и особенностей.
        Укажи:
        - Какие ключевые контракты присутствуют;
        - Основные модули и библиотеки;
        - Какие механизмы могут быть критичны для безопасности и инвесторов.
        Ответ — краткий, понятный и деловой, на русском языке.
    """);

        UserMessage userMessage = new UserMessage("""
        Ниже представлен полный текущий исходный код смарт-контрактов (без diff). 
        Построй краткое резюме:
        
        ```
        %s
        ```
    """.formatted(diff));

        return openAiService.talkToChatGPT(changeSet.getId().toString(), systemMessage, userMessage);
    }

    public String generateInitialAbiSummary(AbiChangeSet changeSet) {
        String abi = changeSet.getGitDiff(); // в данном случае — весь ABI
        abi = CommonUtils.splitSolidityContractsInBatches(abi, 3000).get(0);
        SystemMessage systemMessage = new SystemMessage("""
        Ты — эксперт по API-интерфейсам Ethereum.
        Проанализируй полный ABI контракта и сформируй обзор:
        - Какие публичные функции доступны;
        - Какие события могут быть важны для фронтенда и пользователей;
        - Насколько велик и сложен API.
        Ответ — краткий, профессиональный, на русском языке.
    """);

        UserMessage userMessage = new UserMessage("""
        Вот полный ABI контракта:
        ```
        %s
        ```
    """.formatted(abi));

        return openAiService.talkToChatGPT(changeSet.getId().toString(), systemMessage, userMessage);
    }

}
