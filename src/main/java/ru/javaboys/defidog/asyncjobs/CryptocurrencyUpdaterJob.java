package ru.javaboys.defidog.asyncjobs;

import io.jmix.core.security.SystemAuthenticator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.crypto.CryptocurrencyService;

import java.time.Duration;

@Component
@ConditionalOnProperty(prefix = "coinmarketcap.updater", name = "enabled", havingValue = "true")
public class CryptocurrencyUpdaterJob {

    private final CryptocurrencyService cryptocurrencyService;
    private final SystemAuthenticator systemAuthenticator;
    private final TaskScheduler taskScheduler;

    @Value("${coinmarketcap.updater.interval-in-seconds}")
    private long intervalInSeconds;

    @Autowired
    public CryptocurrencyUpdaterJob(CryptocurrencyService cryptocurrencyService,
                                    SystemAuthenticator systemAuthenticator,
                                    @Qualifier("core_ThreadPoolTaskScheduler") TaskScheduler taskScheduler) {
        this.cryptocurrencyService = cryptocurrencyService;
        this.systemAuthenticator = systemAuthenticator;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void scheduleTask() {
        taskScheduler.scheduleAtFixedRate(this::updateCryptocurrenciesPeriodically, Duration.ofSeconds(intervalInSeconds));
    }

    private void updateCryptocurrenciesPeriodically() {
        systemAuthenticator.runWithSystem(cryptocurrencyService::updateCryptocurrenciesAndDefiProtocolsLogo);
        systemAuthenticator.runWithSystem(cryptocurrencyService::updateCryptocurrenciesInfo);
    }

}
