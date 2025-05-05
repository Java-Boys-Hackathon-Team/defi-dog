package ru.javaboys.defidog.integrations;

import io.jmix.core.DataManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import ru.javaboys.defidog.entity.User;
import ru.javaboys.defidog.integrations.blockchain.BlockchainService;
import ru.javaboys.defidog.integrations.coinmarketcap.CoinMarketCapService;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapResponseDto;
import ru.javaboys.defidog.integrations.etherscan.EtherscanService;
import ru.javaboys.defidog.integrations.etherscan.dto.ContractSourceResponseDto;
import ru.javaboys.defidog.integrations.openai.OpenAiService;
import ru.javaboys.defidog.integrations.telegram.TelegramBotService;
import ru.javaboys.defidog.test_support.AuthenticatedAsAdmin;
import ru.javaboys.defidog.utils.DotenvTestExecutionListener;

import java.math.BigInteger;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestExecutionListeners(
        listeners = DotenvTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@ExtendWith(AuthenticatedAsAdmin.class)
@Slf4j
public class IntegrationsTest {

    @Autowired
    private EtherscanService etherscanService;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CoinMarketCapService coinMarketCapService;

    @Test
    void shouldFetchContractSourceCodeFromEtherscan() {
        String chainId = "1"; // Ethereum Mainnet
        String contractAddress = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48"; // USDC

        ContractSourceResponseDto response = etherscanService.getContractSourceCode(chainId, contractAddress);

        log.info("Etherscan response: {}", response);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("1");
        assertThat(response.getResult()).isNotEmpty();
        assertThat(response.getResult().get(0).getSourceCode()).isNotBlank();
    }

    @Test
    void shouldTalkToChatGPTSuccessfully() {
        String conversationId = UUID.randomUUID().toString();
        SystemMessage systemMessage = new SystemMessage("You are a helpful assistant.");
        UserMessage userMessage = new UserMessage("What's the capital of France?");

        String response = openAiService.talkToChatGPT(conversationId, systemMessage, userMessage);

        log.info("ChatGPT response: {}", response);

        assertThat(response).isNotBlank();
        assertThat(response.toLowerCase()).contains("paris");
    }

    @Test
    void shouldReturnLatestBlockNumber() {
        BigInteger blockNumber = blockchainService.getLastBlockNumber();

        log.info("Blockchain API response: {}", blockNumber);

        assertThat(blockNumber).isNotNull();
        assertThat(blockNumber.longValue()).isGreaterThan(0);
    }

    @Test
    void shouldSendMessageToUser() {

        String message = "Привет из интеграционного теста!";

        var admin = dataManager.load(User.class)
                .query("e.username = ?1", "admin")
                .one();

        telegramBotService.sendMessageToUser(message, admin);
    }


    @Test
    void shouldFetchCryptocurrencyListings() {
        CoinMarketCapResponseDto response = coinMarketCapService.getCryptocurrencyListingsLatest();

        log.info("CoinMarketCap response: {}", response);

        assertThat(response).isNotNull();
        assertThat(response.getData()).isNotEmpty();
        assertThat(response.getData().get(0).getName()).isNotBlank();
    }
}
