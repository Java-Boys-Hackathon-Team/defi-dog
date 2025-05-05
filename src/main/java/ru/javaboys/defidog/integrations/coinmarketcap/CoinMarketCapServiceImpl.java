package ru.javaboys.defidog.integrations.coinmarketcap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapResponseDto;

// https://coinmarketcap.com/api/documentation/
@Service
@Slf4j
public class CoinMarketCapServiceImpl implements CoinMarketCapService {

    private final RestClient coinmarketcapClient;

    public CoinMarketCapServiceImpl(@Qualifier("coinmarketcapClient") RestClient coinmarketcapClient) {
        this.coinmarketcapClient = coinmarketcapClient;
    }

    @Override
    public CoinMarketCapResponseDto getCryptocurrencyListingsLatest() {
        return coinmarketcapClient.get()
                .uri("/v1/cryptocurrency/listings/latest")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CoinMarketCapResponseDto.class);
    }
}
