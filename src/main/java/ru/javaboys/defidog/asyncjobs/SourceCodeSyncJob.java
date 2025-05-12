package ru.javaboys.defidog.asyncjobs;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.javaboys.defidog.asyncjobs.service.ChangeSetInitializationService;
import ru.javaboys.defidog.asyncjobs.updater.SourceCodeUpdater;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceCodeSyncJob {

    private final UnconstrainedDataManager dataManager;
    // Регистрация обработчиков по типу источника
    private final Map<SourceType, SourceCodeUpdater> updaterMap;

    private final ChangeSetInitializationService changeSetInitializationService;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void run() {
        log.info("Запуск джобы синхронизации исходного кода...");

        List<SourceCode> sources = dataManager.load(SourceCode.class)
                .all()
                .list();

        for (SourceCode sourceCode : sources) {
            SourceType sourceType = sourceCode.getSourceType();
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

        try {
            log.info("Создание начальных ChangeSet-ов, если они отсутствуют...");
            changeSetInitializationService.createInitialChangeSetsIfMissing();
        } catch (Exception e) {
            log.error("Ошибка при создании начальных ChangeSet-ов: {}", e.getMessage(), e);
        }

        log.info("Завершена джоба синхронизации исходного кода.");
    }
}
