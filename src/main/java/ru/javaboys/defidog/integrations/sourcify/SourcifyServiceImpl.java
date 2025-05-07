package ru.javaboys.defidog.integrations.sourcify;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.javaboys.defidog.integrations.sourcify.client.ApiClient;
import ru.javaboys.defidog.integrations.sourcify.client.ContractLookupApi;
import ru.javaboys.defidog.integrations.sourcify.dto.GetContract200Response;
import ru.javaboys.defidog.integrations.sourcify.dto.GetV2ContractsChainId200Response;

import java.math.BigDecimal;

// https://docs.sourcify.dev/docs/api/
@Service
@Slf4j
public class SourcifyServiceImpl implements SourcifyService {

    private final ContractLookupApi contractLookupApi;

    public SourcifyServiceImpl(@Qualifier("sourcifyClient") RestClient sourcifyClient) {
        this.contractLookupApi = new ContractLookupApi(new ApiClient(sourcifyClient));
    }

    @Override
    @SneakyThrows
    public GetContract200Response getContract(String chainId, String address, String fields, String omit) {
        return contractLookupApi.getContract(chainId, address, fields, omit);
    }

    @Override
    @SneakyThrows
    public GetV2ContractsChainId200Response getV2ContractsChainId(String chainId, String sort, BigDecimal limit, String afterMatchId) {
        return contractLookupApi.getV2ContractsChainId(chainId, sort, limit, afterMatchId);
    }
}
