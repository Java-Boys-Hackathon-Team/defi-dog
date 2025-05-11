package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CoinMarketCapMetadataDto {
    private Map<String, CryptocurrencyMetadataDto> data;
    private StatusDto status;
}
