package ru.javaboys.defidog.integrations.etherscan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@Slf4j
public class EtherscanConfig {

    @Value("${etherscan.base-url}")
    private String etherscanBaseUrl;

    @Value("${etherscan.api-key}")
    private String etherscanApiKey;

    @Bean
    @Qualifier("etherscanClient")
    public RestClient etherscanClient() {

        var uri = UriComponentsBuilder.fromUriString(etherscanBaseUrl)
                .queryParam("apikey", etherscanApiKey)
                .build()
                .toUri();

        return RestClient.builder()
                .baseUrl(uri)
                .build();
    }
}
