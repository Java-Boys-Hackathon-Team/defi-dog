package ru.javaboys.defidog.repositories;

import io.jmix.core.DataManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javaboys.defidog.entity.ProtocolKind;
import ru.javaboys.defidog.entity.SourceCode;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SourceCodeRepository {

    private final DataManager dataManager;

    public Optional<SourceCode> findFirstSourceCodeByProtocolId(UUID protocolId, ProtocolKind kind) {
        String query = switch (kind) {
            case DEFI -> """
            select s.sources from SmartContract s
            where s.deFiProtocol.id = :protocolId and s.sources is not null
            order by s.createdDate
        """;
            case CRYPTOCURRENCY -> """
            select s.sources from SmartContract s
            where s.cryptocurrency.id = :protocolId and s.sources is not null
            order by s.createdDate
        """;
        };

        return dataManager.load(SourceCode.class)
                .query(query)
                .parameter("protocolId", protocolId)
                .optional();
    }
}
