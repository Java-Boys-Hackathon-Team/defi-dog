package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import lombok.Data;

import java.util.List;

@Data
public class CryptocurrencyMetadataDto {
    private Integer id;
    private String name;
    private String symbol;
    private String slug;
    private String category;
    private String logo;
    private String description;
    private String date_added;
    private String date_launched;
    private List<String> tags;
    private PlatformDto platform;
    private Boolean infinite_supply;
    private Double self_reported_circulating_supply;
    private Double self_reported_market_cap;
    private List<String> self_reported_tags;
    private UrlsDto urls;
    private String notice;
}
