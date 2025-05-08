package ru.javaboys.defidog.integrations.sourcify;

import ru.javaboys.defidog.integrations.sourcify.dto.GetContract200Response;
import ru.javaboys.defidog.integrations.sourcify.dto.GetV2ContractsChainId200Response;

import java.math.BigDecimal;

public interface SourcifyService {
    GetContract200Response getContract(String chainId, String address, String fields, String omit);
    GetV2ContractsChainId200Response getV2ContractsChainId(String chainId, String sort, BigDecimal limit, String afterMatchId);
}
