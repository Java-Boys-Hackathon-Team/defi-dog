package ru.javaboys.defidog.asyncjobs.service;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.asyncjobs.dto.AuditReportResponseDto;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.SecurityScanJobStatus;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;
import ru.javaboys.defidog.integrations.openai.OpenAiService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditReportService {

    private final OpenAiService openAiService;
    private final UnconstrainedDataManager dataManager;

    public AuditReport generateReport(SourceCodeChangeSet codeChangeSet) {
        SourceCode sourceCode = codeChangeSet.getSourceCode();

        AbiChangeSet abiChangeSet = dataManager.load(AbiChangeSet.class)
                .query("select a from AbiChangeSet a where a.sourceCode = :sc and a.commitHash = :hash")
                .parameter("sc", sourceCode)
                .parameter("hash", codeChangeSet.getCommitHash())
                .optional()
                .orElse(null);

        // Сканирование
        List<SourceCodeSecurityScanJob> scanJobs = codeChangeSet.getSecurityScanJobResult();
        List<String> completedOutputs = scanJobs.stream()
                .filter(job -> SecurityScanJobStatus.COMPLETED.getId().equals(job.getStatus()))
                .map(SourceCodeSecurityScanJob::getRawOutput)
                .filter(Objects::nonNull)
                .toList();

        // --- Формируем промпт
        StringBuilder userInput = new StringBuilder();
        userInput.append("Ниже приведены данные аудита исходного кода и интерфейса смарт-контракта:\n\n");
        userInput.append("### Изменения исходного кода:\n").append(codeChangeSet.getChangeSummary()).append("\n\n");

        if (abiChangeSet != null) {
            userInput.append("### Изменения ABI:\n").append(abiChangeSet.getChangeSummary()).append("\n\n");
        }

        if (!completedOutputs.isEmpty()) {
            userInput.append("### Результаты сканирования инструментами статического анализа:\n");
            for (String output : completedOutputs) {
                userInput.append(output).append("\n\n");
            }
        } else {
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

        AuditReportResponseDto result = openAiService.structuredTalkToChatGPT(
                codeChangeSet.getId().toString(),
                systemMessage,
                userMessage,
                AuditReportResponseDto.class
        );

        // --- Сохраняем AuditReport
        AuditReport report = dataManager.create(AuditReport.class);
        report.setSourceCodeChangeSet(codeChangeSet);
        report.setAbiChangeSet(abiChangeSet);
        report.setSummary(result.getMarkdownReport());
        report.setDescription(result.getDescription());
        report.setCriticality(result.getCriticality().getId());
        report.setSmartContract(sourceCode.getSmartContracts().isEmpty()
                ? null
                : sourceCode.getSmartContracts().get(0)); // если связан
        dataManager.save(report);

        return report;
    }
}
