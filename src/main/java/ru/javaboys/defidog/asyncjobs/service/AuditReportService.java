package ru.javaboys.defidog.asyncjobs.service;

import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.javaboys.defidog.asyncjobs.dto.AuditReportResponseDto;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.SecurityScanJobStatus;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;
import ru.javaboys.defidog.integrations.openai.OpenAiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditReportService {

    private final OpenAiService openAiService;
    private final UnconstrainedDataManager dataManager;

    public AuditReport generateReport(SourceCodeChangeSet codeChangeSet) {
        log.info("Начало генерации аудиторского отчета для изменений кода с ID: {}, commit: {}", 
                codeChangeSet.getId(), codeChangeSet.getCommitHash());

        SourceCode sourceCode = codeChangeSet.getSourceCode();
        log.info("Исходный код получен, ID: {}, репозиторий: {}",
                sourceCode.getId(), sourceCode.getRepoUrl());

        log.info("Поиск связанного AbiChangeSet для commit: {}", codeChangeSet.getCommitHash());
        AbiChangeSet abiChangeSet = dataManager.load(AbiChangeSet.class)
                .query("select a from AbiChangeSet a where a.sourceCode = :sc and a.commitHash = :hash")
                .parameter("sc", sourceCode)
                .parameter("hash", codeChangeSet.getCommitHash())
                .optional()
                .orElse(null);

        if (abiChangeSet != null) {
            log.info("Найден AbiChangeSet с ID: {}", abiChangeSet.getId());
        } else {
            log.info("AbiChangeSet не найден для данного commit");
        }

        // Сканирование
        log.info("Получение результатов сканирования безопасности");
        List<SourceCodeSecurityScanJob> scanJobs = codeChangeSet.getSecurityScanJobResult();
        log.info("Найдено {} заданий сканирования", scanJobs.size());

        List<String> completedOutputs = scanJobs.stream()
                .filter(job -> SecurityScanJobStatus.COMPLETED.getId().equals(job.getStatus()))
                .map(SourceCodeSecurityScanJob::getRawOutput)
                .filter(Objects::nonNull)
                .toList();
        log.info("Получено {} завершенных результатов сканирования", completedOutputs.size());

        // --- Формируем промпт
        log.info("Формирование промпта для OpenAI");
        StringBuilder userInput = new StringBuilder();
        userInput.append("Ниже приведены данные аудита исходного кода и интерфейса смарт-контракта:\n\n");
        userInput.append("### Изменения исходного кода:\n").append(codeChangeSet.getChangeSummary()).append("\n\n");
        log.info("Добавлены данные об изменениях исходного кода");

        if (abiChangeSet != null) {
            userInput.append("### Изменения ABI:\n").append(abiChangeSet.getChangeSummary()).append("\n\n");
            log.info("Добавлены данные об изменениях ABI");
        }

        if (!completedOutputs.isEmpty()) {
            log.info("Добавление {} результатов сканирования в промпт", completedOutputs.size());
            userInput.append("### Результаты сканирования инструментами статического анализа:\n");
            for (String output : completedOutputs) {
                userInput.append(output).append("\n\n");
            }
        } else {
            log.info("Результаты сканирования отсутствуют или завершились с ошибкой");
            userInput.append("### Результаты сканирования: отсутствуют или завершились с ошибкой.\n\n");
        }

        SystemMessage systemMessage = new SystemMessage("""
                    Ты — профессиональный аудитoр смарт-контрактов и эксперт по безопасности DeFi-протоколов.
                    На основе данных об изменениях в коде и ABI, а также (если есть) результатов сканеров безопасности,
                    сгенерируй полный аудиторский отчет в формате Markdown.
                    В отчете отрази:
                    - потенциальные риски и уязвимости,
                    - изменение логики или публичного API,
                    - возможные последствия для инвесторов и пользователей,
                    - рекомендации для разработчиков.

                    Кроме того:
                    - укажи краткую характеристику отчета (1-2 предложения),
                    - оцени уровень критичности: CRITICAL / HIGH / NORMAL.
                    Ответ верни в формате JSON со следующими полями:
                    - markdownReport (string, обязательный): отчет в формате Markdown,
                    - description (string, обязательный): краткое описание отчета,
                    - criticality (enum: CRITICAL | HIGH | NORMAL): оценка серьезности.

                    Весь ответ должен быть СТРОГО на русском языке.
                """);


        UserMessage userMessage = new UserMessage(userInput.toString());
        log.info("Подготовлен пользовательский запрос для OpenAI, длина: {} символов", userInput.length());

        log.info("Отправка запроса в OpenAI для генерации аудиторского отчета");
        try {
            AuditReportResponseDto result = openAiService.structuredTalkToChatGPT(
                    codeChangeSet.getId().toString(),
                    systemMessage,
                    userMessage,
                    AuditReportResponseDto.class
            );
            log.info("Получен ответ от OpenAI, уровень критичности: {}", result.getCriticality());
            log.info("Длина отчета: {} символов", result.getMarkdownReport().length());

            // --- Сохраняем AuditReport
            log.info("Создание объекта AuditReport");
            AuditReport report = dataManager.create(AuditReport.class);
            report.setSourceCodeChangeSet(codeChangeSet);
            report.setAbiChangeSet(abiChangeSet);
            report.setSummary(result.getMarkdownReport());
            report.setDescription(result.getDescription());
            report.setCriticality(result.getCriticality().getId());

            if (!sourceCode.getSmartContracts().isEmpty()) {
                log.info("Связывание отчета со смарт-контрактом: {}", sourceCode.getSmartContracts().get(0).getId());
                report.setSmartContract(sourceCode.getSmartContracts().get(0));
            } else {
                log.info("Смарт-контракты не найдены для данного исходного кода");
            }

            AuditReport savedReport = dataManager.save(report);
            log.info("Аудиторский отчет успешно сохранен, ID: {}", savedReport.getId());
            return savedReport;
        } catch (Exception e) {
            log.error("Ошибка при генерации аудиторского отчета: {}", e.getMessage(), e);
            throw e;
        }
    }
}
