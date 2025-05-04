package ru.javaboys.defidog.integrations.etherscan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.javaboys.defidog.integrations.etherscan.dto.ContractSourceResponseDto;

// https://docs.etherscan.io/etherscan-v2
@Service
@Slf4j
public class EtherscanServiceImpl implements EtherscanService {

    private final RestClient etherscanClient;

    public EtherscanServiceImpl(@Qualifier("etherscanClient") RestClient etherscanClient) {
        this.etherscanClient = etherscanClient;
    }

    @Override
    public ContractSourceResponseDto getContractSourceCode(String chainId, String address) {
        return etherscanClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("chainid", chainId)
                        .queryParam("module", "contract")
                        .queryParam("action", "getsourcecode")
                        .queryParam("address", address)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ContractSourceResponseDto.class);
    }
}
