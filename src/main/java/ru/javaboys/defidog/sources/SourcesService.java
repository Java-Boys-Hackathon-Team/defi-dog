package ru.javaboys.defidog.sources;

import java.util.UUID;

public interface SourcesService {
    String getSourceCodeByContractId(UUID contractId);
}
