package ru.javaboys.defidog.integrations.coinmarketcap;

import ru.javaboys.defidog.integrations.coinmarketcap.dto.CoinMarketCapResponseDto;

public interface CoinMarketCapService {
    CoinMarketCapResponseDto getCryptocurrencyListingsLatest();
}
