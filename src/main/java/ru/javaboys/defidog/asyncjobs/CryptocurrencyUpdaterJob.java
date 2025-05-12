package ru.javaboys.defidog.asyncjobs;

import io.jmix.core.security.SystemAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.crypto.CryptocurrencyService;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "coinmarketcap.updater", name = "enabled", havingValue = "true")
public class CryptocurrencyUpdaterJob {

    private final CryptocurrencyService cryptocurrencyService;
    private final SystemAuthenticator systemAuthenticator;

    @Scheduled(fixedRate = 30 * 1000)
    public void updateCryptocurrenciesPeriodically() {
        systemAuthenticator.runWithSystem(cryptocurrencyService::updateCryptocurrenciesAndDefiProtocolsLogo);
        systemAuthenticator.runWithSystem(cryptocurrencyService::updateCryptocurrenciesInfo);
    }
}
