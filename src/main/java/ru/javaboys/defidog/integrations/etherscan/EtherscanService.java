package ru.javaboys.defidog.integrations.etherscan;

import ru.javaboys.defidog.integrations.etherscan.dto.ContractAbiResponseDto;
import ru.javaboys.defidog.integrations.etherscan.dto.ContractSourceResponseDto;

public interface EtherscanService {
    ContractSourceResponseDto getContractSourceCode(String chainId, String address);
    ContractAbiResponseDto getContractAbi(String chainId, String address);
}
