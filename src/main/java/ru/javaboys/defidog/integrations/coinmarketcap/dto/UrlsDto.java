package ru.javaboys.defidog.integrations.coinmarketcap.dto;

import lombok.Data;

import java.util.List;

@Data
public class UrlsDto {
    private List<String> website;
    private List<String> technical_doc;
    private List<String> twitter;
    private List<String> reddit;
    private List<String> message_board;
    private List<String> announcement;
    private List<String> chat;
    private List<String> explorer;
    private List<String> source_code;
}
