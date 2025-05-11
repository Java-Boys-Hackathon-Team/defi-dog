package ru.javaboys.defidog.asyncjobs.updater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.asyncjobs.service.LocalRepositoryIntegratorService;
import ru.javaboys.defidog.asyncjobs.util.CommonUtils;
import ru.javaboys.defidog.asyncjobs.util.SourceStorageService;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;
import ru.javaboys.defidog.integrations.etherscan.EtherscanService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtherscanSourceCodeUpdater implements SourceCodeUpdater, TypedUpdater {

    private final EtherscanService etherscanService;
    private final SourceStorageService storageService;
    private final LocalRepositoryIntegratorService integratorService;

    @Override
    public SourceType getSupportedSourceType() {
        return SourceType.ETHERSCAN;
    }

    @Override
    public String update(SourceCode sourceCode) throws Exception {
        StringBuilder jobLog = new StringBuilder();
        Path repoPath = storageService.getSourceDirectory(sourceCode);
        File repoDir = repoPath.toFile();

        jobLog.append("Источник: ETHERSCAN\n");

        List<SmartContract> contracts = sourceCode.getSmartContracts();
        if (contracts == null || contracts.isEmpty()) {
            throw new IllegalStateException("Нет связанных SmartContract-ов для SourceCode ID=" + sourceCode.getId());
        }

        if (!repoDir.exists()) {
            jobLog.append("Создание нового git-репозитория...\n");
            repoDir.mkdirs();
            Git.init().setDirectory(repoDir).call();
        }

        for (SmartContract contract : contracts) {
            String network = contract.getNetwork();
            String address = contract.getAddress();
            String chainId = CommonUtils.getChainIdByNetworkName(network);

            jobLog.append("Получение данных по контракту: ").append(address).append("\n");

            var codeResponse = etherscanService.getContractSourceCode(chainId, address);
            String sourceText = codeResponse != null && codeResponse.getResult() != null && !codeResponse.getResult().isEmpty()
                    ? codeResponse.getResult().get(0).getSourceCode()
                    : null;

            if (sourceText == null || sourceText.isBlank()) {
                jobLog.append("⚠️ Исходный код не найден для адреса ").append(address).append("\n");
                continue;
            }

            var abiResponse = etherscanService.getContractAbi(chainId, address);
            String abi = abiResponse != null ? abiResponse.getResult() : null;

            // Запись файлов
            File sourceFile = new File(repoDir, "Contract-" + address + ".sol");
            FileUtils.writeStringToFile(sourceFile, sourceText, StandardCharsets.UTF_8);

            if (abi != null && !abi.isBlank()) {
                File abiFile = new File(repoDir, "Contract-" + address + ".abi.json");
                FileUtils.writeStringToFile(abiFile, abi, StandardCharsets.UTF_8);
            }

            jobLog.append("Файлы для контракта ").append(address).append(" записаны.\n");
        }

        // Коммит
        Git git = Git.open(repoDir);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Fetched multiple contracts from Etherscan").call();

        String integrationLog = integratorService.integrateAndProcess(sourceCode, repoPath);
        jobLog.append(integrationLog);

        return jobLog.toString();
    }
}

