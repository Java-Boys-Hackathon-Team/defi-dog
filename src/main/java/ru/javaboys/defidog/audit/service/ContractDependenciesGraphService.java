package ru.javaboys.defidog.audit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.asyncjobs.util.CommonUtils;
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
        List<String> contractBatches = CommonUtils.splitSolidityContractsInBatches(sourceCode, DEFAULT_MAX_TOKENS_PER_BATCH);

        log.info("Разделено на {} батчей по ~{} токенов", contractBatches.size(), DEFAULT_MAX_TOKENS_PER_BATCH);

        List<String> batchGraphs = new ArrayList<>();
        for (int i = 0; i < contractBatches.size(); i++) {
            String batch = contractBatches.get(i);
            log.info("🚀 Генерируем граф для батча #{}", i + 1);

            String graph = generateGraphForBatch(batch, i + 1, contractBatches.size());
            batchGraphs.add(graph);

            // Жёсткая пауза 10 секунд между батчами (или больше если надо)
            sleepSafe(10000);
        }

        log.info("🛠️ Склеиваем {} графов в один", batchGraphs.size());

        // Перед мержем тоже пауза (если мерж большой — лучше выдержать)
        sleepSafe(15000);

        return mergeGraphsWithChatGpt(batchGraphs);
    }

    private void sleepSafe(long millis) {
        try {
            log.info("⏸ Пауза {} мс для избежания 429", millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrupted во время паузы");
        }
    }

    /**
     * Генерация графа для батча контрактов.
     * 
     * @param batchContracts содержимое батча контрактов
     * @param batchNumber номер текущего батча (начиная с 1)
     * @param totalBatches общее количество батчей
     * @return JSON-представление графа
     */
    private String generateGraphForBatch(String batchContracts, int batchNumber, int totalBatches) {
        log.info("🔄 Обрабатываем батч № {} из {}. Осталось: {}",
                batchNumber, totalBatches, totalBatches - batchNumber);

        String conversationId = UUID.randomUUID().toString();
        String userMessage = BUILD_GRAPH_FROM_CODE_PROMPT_TEMPLATE.formatted(batchContracts);

        String response = openAiService.talkToChatGPT(
                conversationId,
                new SystemMessage("Ты помощник по анализу смарт-контрактов."),
                new UserMessage(userMessage)
        );

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            log.info("Успешно сгенерирован граф для батча № {}. Узлов: {}", batchNumber, jsonNode.at("/elements/nodes").size());
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
