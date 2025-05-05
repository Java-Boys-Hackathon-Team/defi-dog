package ru.javaboys.defidog.integrations.coinmarketcap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CoinMarketCapConfig {

    @Value("${coinmarketcap.base-url}")
    private String coinmarketcapBaseUrl;

    @Value("${coinmarketcap.api-key}")
    private String coinmarketcapApiKey;

    @Bean
    @Qualifier("coinmarketcapClient")
    public RestClient coinmarketcapClient() {

        return RestClient.builder()
                .baseUrl(coinmarketcapBaseUrl)
                .defaultHeader("X-CMC_PRO_API_KEY", coinmarketcapApiKey)
                .build();
    }
}
