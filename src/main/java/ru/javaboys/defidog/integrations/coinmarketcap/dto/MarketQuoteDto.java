package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarketQuoteDto {
    @JsonProperty("price")
    private Double price;

    @JsonProperty("volume_24h")
    private Double volume24h;

    @JsonProperty("volume_change_24h")
    private Double volumeChange24h;

    @JsonProperty("percent_change_1h")
    private Double percentChange1h;

    @JsonProperty("percent_change_24h")
    private Double percentChange24h;

    @JsonProperty("percent_change_7d")
    private Double percentChange7d;

    @JsonProperty("percent_change_30d")
    private Double percentChange30d;

    @JsonProperty("percent_change_60d")
    private Double percentChange60d;

    @JsonProperty("percent_change_90d")
    private Double percentChange90d;

    @JsonProperty("market_cap")
    private Double marketCap;

    @JsonProperty("market_cap_dominance")
    private Double marketCapDominance;

    @JsonProperty("fully_diluted_market_cap")
    private Double fullyDilutedMarketCap;

    @JsonProperty("tvl")
    private Double tvl;

    @JsonProperty("last_updated")
    private String lastUpdated;
}
