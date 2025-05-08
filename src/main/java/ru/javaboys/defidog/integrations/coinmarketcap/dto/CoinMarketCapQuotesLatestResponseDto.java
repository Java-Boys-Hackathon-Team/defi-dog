package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CoinMarketCapQuotesLatestResponseDto {
    @JsonProperty("status")
    private StatusDto status;

    @JsonProperty("data")
    private Map<String, CryptocurrencyQuoteDto> data;
}
