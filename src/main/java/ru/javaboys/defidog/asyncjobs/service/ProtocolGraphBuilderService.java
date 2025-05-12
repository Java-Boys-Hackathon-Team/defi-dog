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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolGraphBuilderService {

    private final UnconstrainedDataManager dataManager;
    private final ContractDependenciesGraphService contractDependenciesGraphService;

    // Cache to store results based on source code hash
    private final Map<String, String> sourceCodeCache = new ConcurrentHashMap<>();

    /**
     * Generates a SHA-256 hash for the given source code
     * @param sourceCode the source code to hash
     * @return the SHA-256 hash as a hexadecimal string
     */
    private String generateSourceCodeHash(String sourceCode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(sourceCode.getBytes(StandardCharsets.UTF_8));

            // Convert bytes to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate hash for source code", e);
            // Fallback to a simple hash if SHA-256 is not available
            return String.valueOf(sourceCode.hashCode());
        }
    }

    public void buildGraphProtocol(AuditReport report, SourceCodeChangeSet changeSet) {
        String sourceCode = changeSet.getSourceCode().getLastKnownSourceCode();
        log.info("🔄 Начало генерации графа зависимостей для AuditReport ID: {}", report.getId());

        // Generate hash for the source code
        String sourceCodeHash = generateSourceCodeHash(sourceCode);

        // Check if we have a cached result for this source code
        String jsonGraph;
        if (sourceCodeCache.containsKey(sourceCodeHash)) {
            jsonGraph = sourceCodeCache.get(sourceCodeHash);
            log.info("🔄 Использован кешированный JSON граф для AuditReport ID: {}", report.getId());
        } else {
            // Generate new JSON graph and cache it
            jsonGraph = contractDependenciesGraphService.generateGraphJsonFromSource(sourceCode);
            sourceCodeCache.put(sourceCodeHash, jsonGraph);
            log.info("✅ Сгенерирован новый JSON граф: {}", jsonGraph);
        }

        SmartContract contract = report.getSmartContract();

        if (contract == null) {
            log.warn("⚠️ У AuditReport ID: {} не задан SmartContract — пропуск генерации графа", report.getId());
            return;
        }

        // --- Обновление криптовалюты
        Cryptocurrency crypto = contract.getCryptocurrency();
        if (crypto != null) {
            crypto = dataManager.load(Cryptocurrency.class).id(crypto.getId()).one(); // гарантировано managed
            ContractDependenciesGraph graph = crypto.getDependencyGraph();
            if (graph == null) {
                graph = dataManager.create(ContractDependenciesGraph.class);
                crypto.setDependencyGraph(graph);
            }
            graph.setGraphJson(jsonGraph);
            dataManager.save(crypto); // сохраняем владеющего
            log.info("📦 Граф обновлён для Cryptocurrency ID: {}", crypto.getId());
        }

        // --- Обновление DeFi протокола
        DeFiProtocol defi = contract.getDeFiProtocol();
        if (defi != null) {
            defi = dataManager.load(DeFiProtocol.class).id(defi.getId()).one();
            ContractDependenciesGraph graph = defi.getDependencyGraph();
            if (graph == null) {
                graph = dataManager.create(ContractDependenciesGraph.class);
                defi.setDependencyGraph(graph);
            }
            graph.setGraphJson(jsonGraph);
            dataManager.save(defi);
            log.info("📦 Граф обновлён для DeFiProtocol ID: {}", defi.getId());
        }
    }
}
