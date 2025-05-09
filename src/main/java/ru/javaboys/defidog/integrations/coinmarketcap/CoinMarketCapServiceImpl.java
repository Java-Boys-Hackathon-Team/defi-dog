package ru.javaboys.defidog.integrations.coinmarketcap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapIdMapResponseDto;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapQuotesLatestResponseDto;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapResponseDto;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public CoinMarketCapIdMapResponseDto getCryptocurrencyCoinMarketCapIDMap() {
        return coinmarketcapClient.get()
                .uri("/v1/cryptocurrency/map")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CoinMarketCapIdMapResponseDto.class);
    }

    @Override
    public CoinMarketCapQuotesLatestResponseDto getCryptocurrencyQuotesLatestByIds(List<Integer> ids) {
        String idsParam = ids.stream().map(Object::toString).collect(Collectors.joining(","));

//        String re = coinmarketcapClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/v2/cryptocurrency/quotes/latest")
//                        .queryParam("id", idsParam)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .body(String.class);

        return coinmarketcapClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/cryptocurrency/quotes/latest")
                        .queryParam("id", idsParam)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CoinMarketCapQuotesLatestResponseDto.class);
    }
}
