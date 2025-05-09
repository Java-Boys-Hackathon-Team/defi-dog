package ru.javaboys.defidog.repositories;

import io.jmix.core.DataManager;
import io.jmix.core.FetchPlans;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.entity.SmartContract;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeFiProtocolRepository {

    private final DataManager dataManager;
    private final FetchPlans fetchPlans;

    public Optional<String> findNameById(UUID protocolId) {
        return dataManager.loadValue(
                        "select p.name from DeFiProtocol p where p.id = :protocolId", String.class)
                .parameter("protocolId", protocolId)
                .optional();
    }

    public Optional<String> findDescriptionById(UUID protocolId) {
        return dataManager.loadValue(
                        "select p.description from DeFiProtocol p where p.id = :protocolId", String.class)
                .parameter("protocolId", protocolId)
                .optional();
    }

    public Optional<String> findGraphJsonById(UUID protocolId) {
        return dataManager.loadValue(
                        "select c.graphJson from ContractDependenciesGraph c where c.deFiProtocol.id = :protocolId",
                        String.class)
                .parameter("protocolId", protocolId)
                .optional();
    }

    public List<SmartContract> findContracts(UUID protocolId) {
        DeFiProtocol protocol = dataManager.load(DeFiProtocol.class)
                .id(protocolId)
                .fetchPlan(fetchPlans.builder(DeFiProtocol.class).add("contracts").build())
                .optional()
                .orElse(null);

        return protocol != null ? protocol.getContracts() : Collections.emptyList();
    }
}
