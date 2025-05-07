package ru.javaboys.defidog.integrations.sourcify;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SourcifyConfig {

    @Bean
    @Qualifier("sourcifyClient")
    public RestClient sourcifyClient() {
        return RestClient.builder()
                .baseUrl("https://sourcify.dev/server")
                .build();
    }
}
