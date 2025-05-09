package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CryptocurrencyDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("rank")
    private double rank;

    @JsonProperty("name")
    private String name;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("is_active")
    private int isActive;

    @JsonProperty("status")
    private String status;

    @JsonProperty("first_historical_data")
    private String firstHistoricalData;

    @JsonProperty("last_historical_data")
    private String lastHistoricalData;

    @JsonProperty("platform")
    private PlatformDto platform;
}
