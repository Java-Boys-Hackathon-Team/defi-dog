package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class StatusDto {

    private ZonedDateTime timestamp;

    @JsonProperty("error_code")
    private Integer errorCode;

    @JsonProperty("error_message")
    private String errorMessage;

    private Integer elapsed;

    @JsonProperty("credit_count")
    private Integer creditCount;
}
