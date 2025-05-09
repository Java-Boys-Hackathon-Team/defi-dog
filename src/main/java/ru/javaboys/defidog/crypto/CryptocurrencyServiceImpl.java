package ru.javaboys.defidog.crypto;

import io.jmix.core.DataManager;
import io.jmix.flowui.UiEventPublisher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.integrations.coinmarketcap.CoinMarketCapServiceImpl;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapQuotesLatestResponseDto;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CryptocurrencyQuoteDto;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.MarketQuoteDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CryptocurrencyServiceImpl implements CryptocurrencyService {

    private CoinMarketCapServiceImpl coinMarketCapService;

    private UiEventPublisher uiEventPublisher;

    private DataManager dataManager;

    @Override
    public void updateCryptocurrenciesInfo() {
        List<Integer> cryptoIds =
                dataManager.loadValue("select c.cmcId from Cryptocurrency c", Integer.class)
                .list();

        CoinMarketCapQuotesLatestResponseDto response =
                coinMarketCapService.getCryptocurrencyQuotesLatestByIds(cryptoIds);

        for (Map.Entry<String, CryptocurrencyQuoteDto> cr : response.getData().entrySet()) {
            updateCryptoByDto(cr.getValue());
        }
//        uiEventPublisher.publishEventForUsers(
//                new CryptocurrencyReloadEvent(this), null);
    }

    private void updateCryptoByDto(CryptocurrencyQuoteDto dto) {

        Integer cmcId = dto.getId();

        // Загрузка существующей сущности по CMC_ID
        Cryptocurrency crypto = dataManager.load(Cryptocurrency.class)
                .query("select c from Cryptocurrency c where c.cmcId = :cmcId")
                .parameter("cmcId", cmcId)
                .optional()
                .orElse(null);

        if (crypto != null) {
            crypto.setCmcRank(dto.getCmcRank());
            crypto.setName(dto.getName());
            crypto.setTicker(dto.getSymbol());

//                if (dto.getTotalSupply() != null)
//                    crypto.setTotalSupply(BigDecimal.valueOf(dto.getTotalSupply()));

            MarketQuoteDto usd = dto.getQuote() != null ? dto.getQuote().get("USD") : null;
            if (usd != null) {
                if (usd.getPrice() != null)
                    crypto.setPrice(BigDecimal.valueOf(usd.getPrice()));
                if (usd.getMarketCap() != null)
                    crypto.setMarketCap(BigDecimal.valueOf(usd.getMarketCap()));
            }

            // Сохраняем обновленную сущность
            dataManager.save(crypto);
        }
    }
}
