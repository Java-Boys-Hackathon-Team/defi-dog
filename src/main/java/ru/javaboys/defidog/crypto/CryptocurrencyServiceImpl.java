package ru.javaboys.defidog.crypto;

import io.jmix.core.DataManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.integrations.coinmarketcap.CoinMarketCapServiceImpl;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class CryptocurrencyServiceImpl implements CryptocurrencyService {

    private CoinMarketCapServiceImpl coinMarketCapService;

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
    }

    @Override
    public void updateCryptocurrenciesAndDefiProtocolsLogo() {
        updateLogos(
                "select c.cmcId from Cryptocurrency c where c.cmcId is not null and c.logoImage is null",
                this::updateCryptoLogoByDto
        );

        updateLogos(
                "select df.cmcId from DeFiProtocol df where df.cmcId is not null and df.logoImage is null",
                this::updateDexLogoByDto
        );
    }

    private <T> void updateLogos(String query, Consumer<CryptocurrencyMetadataDto> updater) {
        List<Integer> cmcIds = dataManager.loadValue(query, Integer.class).list();
        if (cmcIds.isEmpty()) return;

        CoinMarketCapMetadataDto response = coinMarketCapService.getCryptocurrencyMetadataByIds(cmcIds);
        response.getData().values().forEach(updater);
    }

    private void updateCryptoLogoByDto(CryptocurrencyMetadataDto dto) {
        updateLogo(Cryptocurrency.class, "Cryptocurrency", dto.getId(), dto.getLogo());
    }

    private void updateDexLogoByDto(CryptocurrencyMetadataDto dto) {
        updateLogo(DeFiProtocol.class, "DeFiProtocol", dto.getId(), dto.getLogo());
    }

    private <T> void updateLogo(Class<T> entityClass, String entityName, Integer cmcId, String logoUrl) {
        T entity = dataManager.load(entityClass)
                .query("select e from " + entityName + " e where e.cmcId = :cmcId")
                .parameter("cmcId", cmcId)
                .optional()
                .orElse(null);

        if (entity != null) {
            try (InputStream in = new URL(logoUrl).openStream()) {
                byte[] imageBytes = in.readAllBytes();

                if (entity instanceof Cryptocurrency c) {
                    c.setLogoImage(imageBytes);
                    dataManager.save(c);
                } else if (entity instanceof DeFiProtocol d) {
                    d.setLogoImage(imageBytes);
                    dataManager.save(d);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to download image: " + logoUrl, e);
            }
        }
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

            MarketQuoteDto usd = dto.getQuote() != null ? dto.getQuote().get("USD") : null;
            if (usd != null) {
                if (usd.getPrice() != null)
                    crypto.setPrice(BigDecimal.valueOf(usd.getPrice()));
                if (usd.getMarketCap() != null)
                    crypto.setMarketCap(BigDecimal.valueOf(usd.getMarketCap()));
                if (usd.getPercentChange24h() != null)
                    crypto.setPercentChange24h(BigDecimal.valueOf(usd.getPercentChange24h()));
            }

            // Сохраняем обновленную сущность
            dataManager.save(crypto);
        }
    }
}
