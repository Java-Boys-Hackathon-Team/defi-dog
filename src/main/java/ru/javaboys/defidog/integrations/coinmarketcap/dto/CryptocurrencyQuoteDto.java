package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CryptocurrencyQuoteDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("num_market_pairs")
    private int numMarketPairs;

    @JsonProperty("date_added")
    private String dateAdded;

    @JsonProperty("tags")
    private List<TagDto> tags;

    @JsonProperty("max_supply")
    private Double maxSupply;

    @JsonProperty("circulating_supply")
    private Double circulatingSupply;

    @JsonProperty("total_supply")
    private Double totalSupply;

    @JsonProperty("is_active")
    private int isActive;

    @JsonProperty("infinite_supply")
    private boolean infiniteSupply;

    @JsonProperty("platform")
    private PlatformDto platform;

    @JsonProperty("cmc_rank")
    private Integer cmcRank;

    @JsonProperty("is_fiat")
    private int isFiat;

    @JsonProperty("self_reported_circulating_supply")
    private Double selfReportedCirculatingSupply;

    @JsonProperty("self_reported_market_cap")
    private Double selfReportedMarketCap;

    @JsonProperty("tvl_ratio")
    private Double tvlRatio;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("quote")
    private Map<String, MarketQuoteDto> quote;
}
