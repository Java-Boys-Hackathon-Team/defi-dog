package ru.javaboys.defidog.integrations.dedaub;

import ru.javaboys.defidog.integrations.dedaub.dto.DecompilationDto;

import java.time.Duration;

public interface DedaubService {
    DecompilationDto decompileSmartContractBytecode(String bytecode, Duration timeout, long sleepInterval);
}
