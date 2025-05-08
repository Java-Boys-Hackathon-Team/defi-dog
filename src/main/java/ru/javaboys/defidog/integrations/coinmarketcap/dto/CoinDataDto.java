package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CoinDataDto {
    private Long id;
    private String name;
    private String symbol;
    private String slug;

    @JsonProperty("cmc_rank")
    private Integer cmcRank;

    @JsonProperty("num_market_pairs")
    private Integer numMarketPairs;

    @JsonProperty("circulating_supply")
    private BigDecimal circulatingSupply;

    @JsonProperty("total_supply")
    private BigDecimal totalSupply;

    @JsonProperty("max_supply")
    private BigDecimal maxSupply;

    @JsonProperty("infinite_supply")
    private boolean infiniteSupply;

    @JsonProperty("last_updated")
    private ZonedDateTime lastUpdated;

    @JsonProperty("date_added")
    private ZonedDateTime dateAdded;

    private List<String> tags;

    private Object platform; // может быть null или объект, если понадобится — можно заменить на PlatformDto

    @JsonProperty("self_reported_circulating_supply")
    private BigDecimal selfReportedCirculatingSupply;

    @JsonProperty("self_reported_market_cap")
    private BigDecimal selfReportedMarketCap;

    private Map<String, QuoteDto> quote;
}
