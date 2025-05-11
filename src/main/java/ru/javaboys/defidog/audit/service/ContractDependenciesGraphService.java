package ru.javaboys.defidog.audit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.integrations.openai.OpenAiService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractDependenciesGraphService {
    public static final String MERGE_GRAPHS_PROMPT = """
            У тебя есть несколько JSON-графов вызовов смарт-контрактов в формате Cytoscape.js.

            Твоя задача:
            - Объединить все графы в один общий.
            - Удалить дубликаты узлов и связей.
            - Сохрани структуру строго в формате Cytoscape.js:
            {
              "elements": {
                "nodes": [
                  { "data": { "id": "contract_id_1", "label": "ContractName1" }},
                  ...
                ],
                "edges": [
                  { "data": { "source": "contract_id_1", "target": "contract_id_2" }},
                  ...
                ]
              }
            }

            ⚠️ Ответ строго в виде JSON (без пояснений, заголовков, комментариев, markdown или лишнего форматирования).
            """;
    public static final String BUILD_GRAPH_FROM_CODE_PROMPT_TEMPLATE = """
            Проанализируй следующие контракты Solidity и построй общий граф вызовов (calls/dependencies) между ними.

            Формат результата — строго JSON в формате Cytoscape.js:
            {
              "elements": {
                "nodes": [
                  { "data": { "id": "contract_id_1", "label": "ContractName1" }},
                  ...
                ],
                "edges": [
                  { "data": { "source": "contract_id_1", "target": "contract_id_2" }},
                  ...
                ]
              }
            }

            ⚠️ Никогда не добавляй пояснений, заголовков, markdown (` ```json `) или каких-либо комментариев. Только валидный JSON.

            Исходный код:
            %s
            """;

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    private static final int DEFAULT_MAX_TOKENS_PER_BATCH = 3000;

    /**
     * Полная цепочка генерации общего графа из большого исходного кода.
     */
    public String generateGraphJsonFromSource(String sourceCode) {
        List<String> contractBatches = splitSolidityContractsInBatches(sourceCode, DEFAULT_MAX_TOKENS_PER_BATCH);

        log.info("Разделено на {} батчей по ~{} токенов", contractBatches.size(), DEFAULT_MAX_TOKENS_PER_BATCH);

        List<String> batchGraphs = new ArrayList<>();
        for (int i = 0; i < contractBatches.size(); i++) {
            String batch = contractBatches.get(i);
            log.info("Генерируем граф для батча #{}", i + 1);
            String graph = generateGraphForBatch(batch);
            batchGraphs.add(graph);
        }

        log.info("Склеиваем {} графов в один", batchGraphs.size());
        return mergeGraphsWithChatGpt(batchGraphs);
    }

    /**
     * Разделяет исходный код по контрактам (по комментариям // ===== ... =====).
     */
    private List<String> splitSolidityContracts(String sourceCode) {
        String[] parts = sourceCode.split("(?=// ===== )");
        List<String> contracts = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                contracts.add(trimmed);
            }
        }
        return contracts;
    }

    /**
     * Группировка контрактов по ~максимальному количеству токенов.
     */
    private List<String> splitSolidityContractsInBatches(String sourceCode, int maxTokensPerBatch) {
        List<String> contracts = splitSolidityContracts(sourceCode);
        List<String> batches = new ArrayList<>();

        StringBuilder currentBatch = new StringBuilder();
        int currentTokens = 0;

        for (String contract : contracts) {
            int estimatedTokens = countTokens(contract);

            if (currentTokens + estimatedTokens > maxTokensPerBatch) {
                batches.add(currentBatch.toString());
                currentBatch = new StringBuilder();
                currentTokens = 0;
            }

            currentBatch.append(contract).append("\n\n");
            currentTokens += estimatedTokens;
        }

        if (currentBatch.length() > 0) {
            batches.add(currentBatch.toString());
        }

        return batches;
    }

    /**
     * Оценка токенов по длине текста.
     */
    private int countTokens(String text) {
        return text.length() / 4;
    }

    /**
     * Генерация графа для батча контрактов.
     */
    private String generateGraphForBatch(String batchContracts) {
        String conversationId = UUID.randomUUID().toString();
        String userMessage = BUILD_GRAPH_FROM_CODE_PROMPT_TEMPLATE.formatted(batchContracts);

        String response = openAiService.talkToChatGPT(
                conversationId,
                new SystemMessage("Ты помощник по анализу смарт-контрактов."),
                new UserMessage(userMessage)
        );

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            log.info("Успешно сгенерирован граф для батча. Узлов: {}", jsonNode.at("/elements/nodes").size());
            return response;
        } catch (Exception e) {
            log.error("Ошибка парсинга JSON графа от OpenAI: {}", e.getMessage());
            return "{\"elements\":{\"nodes\":[],\"edges\":[]}}";
        }
    }

    /**
     * Склейка всех батч-графов в один.
     */
    private String mergeGraphsWithChatGpt(List<String> graphs) {
        String conversationId = UUID.randomUUID().toString();
        StringBuilder prompt = new StringBuilder(MERGE_GRAPHS_PROMPT + "\n\n");

        for (int i = 0; i < graphs.size(); i++) {
            prompt.append("Граф #").append(i + 1).append(":\n").append(graphs.get(i)).append("\n\n");
        }

        String rowResponse = openAiService.talkToChatGPT(
                conversationId,
                new SystemMessage("Ты помощник по анализу графов смарт-контрактов."),
                new UserMessage(prompt.toString())
        );

        String response = cleanGptJsonResponse(rowResponse);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            log.info("Успешно склеен общий граф. Узлов: {}", jsonNode.at("/elements/nodes").size());
            return response;
        } catch (Exception e) {
            log.error("Ошибка парсинга объединенного графа от OpenAI: {}", e.getMessage());
            return "{\"elements\":{\"nodes\":[],\"edges\":[]}}";
        }
    }

    private String cleanGptJsonResponse(String gptResponse) {
        if (gptResponse == null) {
            return "";
        }
        // Убираем начало и конец markdown, пробелы и переносы
        return gptResponse
                .replaceAll("(?i)^\\s*```(?:json)?\\s*", "")
                .replaceAll("(?i)\\s*```\\s*$", "")
                .trim();
    }
}
