package ru.javaboys.defidog.integrations;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import ru.javaboys.defidog.integrations.etherscan.EtherscanService;
import ru.javaboys.defidog.integrations.etherscan.dto.ContractSourceResponseDto;
import ru.javaboys.defidog.utils.DotenvTestExecutionListener;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestExecutionListeners(
        listeners = DotenvTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Slf4j
public class IntegrationsTest {

    @Autowired
    private EtherscanService etherscanService;

    @Test
    void shouldFetchContractSourceCodeFromEtherscan() {
        // Пример адреса и chainId для Ethereum Mainnet
        String chainId = "1"; // Ethereum Mainnet
        String contractAddress = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48"; // USDC

        ContractSourceResponseDto response = etherscanService.getContractSourceCode(chainId, contractAddress);

        log.info("Etherscan response: {}", response);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("1");
        assertThat(response.getResult()).isNotEmpty();
        assertThat(response.getResult().get(0).getSourceCode()).isNotBlank();
    }
}
