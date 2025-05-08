package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CoinMarketCapIdMapResponseDto {
    @JsonProperty("data")
    private List<CryptocurrencyDto> data;

    @JsonProperty("status")
    private StatusDto status;
}
