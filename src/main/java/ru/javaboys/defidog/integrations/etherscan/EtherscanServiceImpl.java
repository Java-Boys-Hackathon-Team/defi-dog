package ru.javaboys.defidog.integrations.etherscan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.javaboys.defidog.integrations.etherscan.dto.ContractSourceResponseDto;

@Service
@Slf4j
public class EtherscanServiceImpl implements EtherscanService {

    private final RestClient etherscanClient;

    @Value("${etherscan.api-key}")
    private String etherscanApiKey;

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
//                        .queryParam("apikey", etherscanApiKey)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Etherscan error: {}", response);
                })
                .body(ContractSourceResponseDto.class);
    }
}
