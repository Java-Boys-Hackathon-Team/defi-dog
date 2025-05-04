package ru.javaboys.defidog.integrations.etherscan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContractSourceResultDto {

    @JsonProperty("SourceCode")
    private String sourceCode;

    @JsonProperty("ABI")
    private String abi;

    @JsonProperty("ContractName")
    private String contractName;

    @JsonProperty("CompilerVersion")
    private String compilerVersion;

    @JsonProperty("OptimizationUsed")
    private String optimizationUsed;

    @JsonProperty("Runs")
    private String runs;

    @JsonProperty("ConstructorArguments")
    private String constructorArguments;

    @JsonProperty("EVMVersion")
    private String evmVersion;

    @JsonProperty("Library")
    private String library;

    @JsonProperty("LicenseType")
    private String licenseType;

    @JsonProperty("Proxy")
    private String proxy;

    @JsonProperty("Implementation")
    private String implementation;

    @JsonProperty("SwarmSource")
    private String swarmSource;

    @JsonProperty("SimilarMatch")
    private String similarMatch;
}
