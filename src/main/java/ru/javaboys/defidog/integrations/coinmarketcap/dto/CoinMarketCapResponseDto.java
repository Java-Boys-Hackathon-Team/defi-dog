package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoinMarketCapResponseDto {
    private List<CoinDataDto> data;
    private StatusDto status;
}