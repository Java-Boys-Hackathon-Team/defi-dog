package ru.javaboys.defidog.asyncjobs.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        value = "defi-dog.scheduling.enable", havingValue = "true", matchIfMissing = true
)
public class SchedulingConfiguration {
}
