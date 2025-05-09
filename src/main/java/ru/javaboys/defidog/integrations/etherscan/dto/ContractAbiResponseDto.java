package ru.javaboys.defidog.integrations.etherscan.dto;

import lombok.Data;

@Data
public class ContractAbiResponseDto {
    private String status;
    private String message;
    private String result;
}
