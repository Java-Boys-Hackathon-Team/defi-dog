package ru.javaboys.defidog.asyncjobs.service;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.audit.service.ContractDependenciesGraphService;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.ContractDependenciesGraph;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolGraphBuilderService {

    private final UnconstrainedDataManager dataManager;
    private final ContractDependenciesGraphService contractDependenciesGraphService;

    public void buildGraphProtocol(AuditReport report, SourceCodeChangeSet changeSet) {
        String sourceCode = changeSet.getSourceCode().getLastKnownSourceCode();
        log.info("🔄 Начало генерации графа зависимостей для AuditReport ID: {}", report.getId());

        String jsonGraph = contractDependenciesGraphService.generateGraphJsonFromSource(sourceCode);
        log.info("✅ Сгенерирован JSON граф: {}", jsonGraph);

        SmartContract contract = report.getSmartContract();

        if (contract == null) {
            log.warn("⚠️ У AuditReport ID: {} не задан SmartContract — пропуск генерации графа", report.getId());
            return;
        }

        // Криптовалюта
        Cryptocurrency crypto = contract.getCryptocurrency();
        if (crypto != null) {
            ContractDependenciesGraph graph = crypto.getDependencyGraph() != null
                    ? crypto.getDependencyGraph()
                    : dataManager.create(ContractDependenciesGraph.class);
            graph.setGraphJson(jsonGraph);
            crypto.setDependencyGraph(graph);
            log.info("📦 Обновлён/создан граф для Cryptocurrency ID: {}", crypto.getId());
        }

        // DeFi-протокол
        DeFiProtocol defi = contract.getDeFiProtocol();
        if (defi != null) {
            ContractDependenciesGraph graph = defi.getDependencyGraph() != null
                    ? defi.getDependencyGraph()
                    : dataManager.create(ContractDependenciesGraph.class);
            graph.setGraphJson(jsonGraph);
            defi.setDependencyGraph(graph);
            log.info("📦 Обновлён/создан граф для DeFiProtocol ID: {}", defi.getId());
        }
    }
}
