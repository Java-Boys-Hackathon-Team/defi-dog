package ru.javaboys.defidog.asyncjobs.updater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javaboys.defidog.entity.SourceType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class SourceCodeUpdaterConfig {

    @Bean
    public Map<SourceType, SourceCodeUpdater> updaterMap(List<SourceCodeUpdater> updaters) {
        log.info("SourceCodeUpdater beans: {}", updaters);
        return updaters.stream()
                .collect(Collectors.toMap(
                        updater -> {
                            if (updater instanceof TypedUpdater typed) {
                                return typed.getSupportedSourceType();
                            } else {
                                throw new IllegalStateException("Updater must implement TypedUpdater");
                            }
                        },
                        updater -> updater
                ));
    }
}
