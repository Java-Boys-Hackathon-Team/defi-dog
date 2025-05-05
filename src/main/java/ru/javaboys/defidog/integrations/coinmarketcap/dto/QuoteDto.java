package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class QuoteDto {

    private BigDecimal price;

    @JsonProperty("volume_24h")
    private BigDecimal volume24h;

    @JsonProperty("volume_change_24h")
    private BigDecimal volumeChange24h;

    @JsonProperty("percent_change_1h")
    private BigDecimal percentChange1h;

    @JsonProperty("percent_change_24h")
    private BigDecimal percentChange24h;

    @JsonProperty("percent_change_7d")
    private BigDecimal percentChange7d;

    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("market_cap_dominance")
    private BigDecimal marketCapDominance;

    @JsonProperty("fully_diluted_market_cap")
    private BigDecimal fullyDilutedMarketCap;

    @JsonProperty("last_updated")
    private ZonedDateTime lastUpdated;
}
