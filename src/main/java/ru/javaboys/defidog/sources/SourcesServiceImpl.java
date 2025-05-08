package ru.javaboys.defidog.sources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourcesServiceImpl implements SourcesService {

    @Override
    public String getSourceCodeByContractId(UUID contractId) {
        return "";
    }
}
