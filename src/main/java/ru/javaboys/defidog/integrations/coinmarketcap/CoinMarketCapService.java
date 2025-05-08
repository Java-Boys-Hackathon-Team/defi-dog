package ru.javaboys.defidog.integrations.coinmarketcap;

import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapIdMapResponseDto;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapQuotesLatestResponseDto;
import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapResponseDto;

import java.util.List;

public interface CoinMarketCapService {
    CoinMarketCapResponseDto getCryptocurrencyListingsLatest();

    CoinMarketCapIdMapResponseDto getCryptocurrencyCoinMarketCapIDMap();

    CoinMarketCapQuotesLatestResponseDto getCryptocurrencyQuotesLatestByIds(List<Integer> ids);
}
