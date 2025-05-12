package ru.javaboys.defidog.asyncjobs.util;

import ru.javaboys.defidog.entity.BlockchainNetwork;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    /**
     * Получение имени сети по идентификатору цепочки.
     */
    public static String getChainIdByNetworkName(BlockchainNetwork network) {
        return switch (network) {
            case ETHEREUM -> "1";
            case POLYGON -> "137";
            case BSC -> "56";
            case SOLANA-> "101";
            default -> throw new IllegalArgumentException("Unsupported network name: " + network);
        };
    }

    /**
     * Группировка контрактов по ~максимальному количеству токенов.
     */
    public static List<String> splitSolidityContractsInBatches(String sourceCode, int maxTokensPerBatch) {
        List<String> contracts = splitSolidityContracts(sourceCode);
        List<String> batches = new ArrayList<>();

        StringBuilder currentBatch = new StringBuilder();
        int currentTokens = 0;

        for (String contract : contracts) {
            int estimatedTokens = countTokens(contract);

            if (currentTokens + estimatedTokens > maxTokensPerBatch) {
                batches.add(currentBatch.toString());
                currentBatch = new StringBuilder();
                currentTokens = 0;
            }

            currentBatch.append(contract).append("\n\n");
            currentTokens += estimatedTokens;
        }

        if (currentBatch.length() > 0) {
            batches.add(currentBatch.toString());
        }

        return batches;
    }

    /**
     * Разделяет исходный код по контрактам (по комментариям // ===== ... =====).
     */
    private static List<String> splitSolidityContracts(String sourceCode) {
        String[] parts = sourceCode.split("(?=// ===== )");
        List<String> contracts = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                contracts.add(trimmed);
            }
        }
        return contracts;
    }

    /**
     * Оценка токенов по длине текста.
     */
    private static int countTokens(String text) {
        return text.length() / 4;
    }
}
