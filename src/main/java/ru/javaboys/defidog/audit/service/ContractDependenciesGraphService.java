package ru.javaboys.defidog.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.audit.dto.ContractDependenciesGraphDto;
import ru.javaboys.defidog.entity.ProtocolKind;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.integrations.openai.OpenAiService;
import ru.javaboys.defidog.repositories.SourceCodeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractDependenciesGraphService {

    private final OpenAiService openAiService;

    private final SourceCodeRepository sourceCodeRepository;


    private ContractDependenciesGraphDto generateGraph(UUID protocolId, ProtocolKind protocolKind) {
        Optional<SourceCode> sourceCodeOpt = sourceCodeRepository.findFirstSourceCodeByProtocolId(protocolId, protocolKind);

        if (sourceCodeOpt.isEmpty() || sourceCodeOpt.get().getLastKnownSourceCode() == null) {
            return new ContractDependenciesGraphDto(new ContractDependenciesGraphDto.Elements(List.of(), List.of()));
        }

        String sourceCode = sourceCodeOpt.get().getLastKnownSourceCode();

        String conversationId = UUID.randomUUID().toString();
        SystemMessage systemMessage = new SystemMessage("""
        Ты — помощник по анализу смарт-контрактов. У тебя есть несколько Solidity-файлов, содержащих смарт-контракты одного проекта (например, Uniswap, Aave, Compound и т.д.).
        """);

        String userMessage = """
        На входе — исходный код одного или нескольких смарт-контрактов на Solidity, участвующих в работе DeFi-протокола.
        Твоя задача — проанализировать эти контракты и построить ориентированный граф вызовов (calls/dependencies) между ними.
        Формат результата — JSON, подходящий для визуализации в Cytoscape.js:
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
        Где:
        - `nodes` — это уникальные смарт-контракты (по имени),
        - `edges` — направленные связи: от вызывающего контракта к вызываемому (`source` → `target`),
        - не добавляй лишние поля,
        - используем UUID или хеши как `id`,
        - порядок связей важен: визуализация ориентирована слева направо или сверху вниз.
        
        ⚠️ Никогда не добавляй пояснений, заголовков, форматирования, markdown (` ```json `), или каких-либо комментариев. Твой ответ — это только валидный JSON в чистом виде, как plain text.
        
        Исходный код:
        """ + sourceCode;

        String jsonGraph = openAiService.talkToChatGPT(
                conversationId,
                systemMessage,
                new UserMessage(userMessage)
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonGraph, ContractDependenciesGraphDto.class);
        } catch (Exception e) {
            log.error("Ошибка при парсинге графа из OpenAI. Protocol ID: {}, ответ: {}", protocolId, jsonGraph, e);

            // Возвращаем пустой граф
            return new ContractDependenciesGraphDto(
                    new ContractDependenciesGraphDto.Elements(
                            List.of(), // пустой список узлов
                            List.of()  // пустой список ребер
                    )
            );
        }
    }

    public String generateGraphAsJson(UUID protocolId, ProtocolKind protocolKind) {
        ContractDependenciesGraphDto graphDto = generateGraph(protocolId, protocolKind);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(graphDto);
        } catch (Exception e) {
            log.error("Ошибка при сериализации графа в JSON. Protocol ID: {}", protocolId, e);
            return "{\"elements\":{\"nodes\":[],\"edges\":[]}}"; // Возврат пустого графа как строка
        }
    }
}
