package ru.javaboys.defidog.integrations.dedaub;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DedaubConfig {

    @Value("${dedaub.base-url}")
    private String dedaubBaseUrl;

    @Value("${dedaub.api-key}")
    private String dedaubApiKey;

    @Bean
    @Qualifier("dedaubClient")
    public RestClient dedaubClient() {

        return RestClient.builder()
                .baseUrl(dedaubBaseUrl)
                // TODO: Чтобы работал API Key нужно купить подписку на сервис Dedaub https://app.dedaub.com/pricing
//                .defaultHeader("x-api-key", dedaubApiKey)
                .build();
    }
}
