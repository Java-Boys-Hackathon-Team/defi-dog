package ru.javaboys.defidog.asyncjobs.updater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import ru.javaboys.defidog.asyncjobs.service.LocalRepositoryIntegratorService;
import ru.javaboys.defidog.asyncjobs.util.CommonUtils;
import ru.javaboys.defidog.asyncjobs.util.SourceStorageService;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;
import ru.javaboys.defidog.integrations.etherscan.EtherscanService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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

        String chainId = CommonUtils.getChainIdByNetworkName(sourceCode.getSmartContracts().get(0).getNetwork());

        String contractAddress = sourceCode.getSmartContracts().get(0).getAddress();

        // Получение исходников
        var codeResponse = etherscanService.getContractSourceCode(chainId, contractAddress);
        String sourceText = codeResponse != null && codeResponse.getResult() != null && !codeResponse.getResult().isEmpty()
                ? codeResponse.getResult().get(0).getSourceCode()
                : null;

        if (sourceText == null || sourceText.isBlank()) {
            throw new IllegalStateException("Не удалось получить исходный код с Etherscan");
        }

        // Получение ABI
        var abiResponse = etherscanService.getContractAbi(chainId, contractAddress);
        String abi = abiResponse != null ? abiResponse.getResult() : null;

        // Подготовка локального репо
        if (!repoDir.exists()) {
            jobLog.append("Создание нового git-репозитория...\n");
            repoDir.mkdirs();
            Git.init().setDirectory(repoDir).call();
        }

        // Запись файлов
        File sourceFile = new File(repoDir, "Contract.sol");
        FileUtils.writeStringToFile(sourceFile, sourceText, StandardCharsets.UTF_8);

        if (abi != null) {
            File abiFile = new File(repoDir, "contract.abi.json");
            FileUtils.writeStringToFile(abiFile, abi, StandardCharsets.UTF_8);
        }

        // Коммит
        Git git = Git.open(repoDir);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Fetched from Etherscan").call();

        // Общая пост-обработка
        String integrationLog = integratorService.integrateAndProcess(sourceCode, repoPath);
        jobLog.append(integrationLog);

        return jobLog.toString();
    }
}

