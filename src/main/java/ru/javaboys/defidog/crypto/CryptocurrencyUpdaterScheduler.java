package ru.javaboys.defidog.crypto;

import io.jmix.core.security.SystemAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "coinmarketcap.updater", name = "enabled", havingValue = "true")
public class CryptocurrencyUpdaterScheduler {

    private final CryptocurrencyService cryptocurrencyService;
    private final SystemAuthenticator systemAuthenticator;

    @Scheduled(fixedRate = 30 * 1000)
    public void updateCryptocurrenciesPeriodically() {
        systemAuthenticator.runWithSystem(cryptocurrencyService::updateCryptocurrenciesInfo);
    }
}
