package ru.javaboys.defidog.integrations.etherscan.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractSourceResponseDto {
    private String status;
    private String message;
    private List<ContractSourceResultDto> result;
}
