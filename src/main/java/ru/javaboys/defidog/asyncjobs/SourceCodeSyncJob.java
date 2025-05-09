package ru.javaboys.defidog.asyncjobs;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.asyncjobs.updater.SourceCodeUpdater;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceCodeSyncJob {

    private final UnconstrainedDataManager dataManager;
    // Регистрация обработчиков по типу источника
    private final Map<SourceType, SourceCodeUpdater> updaterMap;

    @Scheduled(fixedDelay = 30000)
    public void run() {
        log.info("Запуск джобы синхронизации исходного кода...");

        List<SourceCode> sources = dataManager.load(SourceCode.class)
                .all()
                .list();

        for (SourceCode sourceCode : sources) {
            SourceType sourceType = SourceType.fromId(sourceCode.getSourceType());
            if (sourceType == null) {
                log.warn("Неизвестный SourceType: {}", sourceCode.getSourceType());
                continue;
            }

            SourceCodeUpdater updater = updaterMap.get(sourceType);
            if (updater == null) {
                log.warn("Не найден обработчик для SourceType: {}", sourceType);
                continue;
            }

            try {
                log.info("Обработка источника ID={} тип={}", sourceCode.getId(), sourceType);
                String resultLog = updater.update(sourceCode);
                sourceCode.setSyncJobResult(resultLog);
            } catch (Exception e) {
                String errorMsg = "Ошибка при обработке SourceCode ID=%s: %s"
                        .formatted(sourceCode.getId(), e.getMessage());
                log.error(errorMsg, e);
                sourceCode.setSyncJobResult(errorMsg);
            } finally {
                sourceCode.setFetchedAt(OffsetDateTime.now());
                dataManager.save(sourceCode);
            }
        }

        log.info("Завершена джоба синхронизации исходного кода.");
    }
}
