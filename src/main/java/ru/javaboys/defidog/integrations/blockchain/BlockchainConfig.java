package ru.javaboys.defidog.integrations.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class BlockchainConfig {

    @Value("${blockchain.node-url}")
    private String nodeUrl;

    @Bean
    public Web3j getWeb3j() {
        return Web3j.build(new HttpService(nodeUrl));
    }
}

