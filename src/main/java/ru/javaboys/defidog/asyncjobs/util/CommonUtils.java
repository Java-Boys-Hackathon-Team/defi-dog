package ru.javaboys.defidog.asyncjobs.util;

import ru.javaboys.defidog.entity.BlockchainNetwork;

public class CommonUtils {
    public static String getChainIdByNetworkName(BlockchainNetwork network) {
        return switch (network) {
            case ETHEREUM -> "1";
            case POLYGON -> "137";
            case BSC -> "56";
            case SOLANA-> "101";
            default -> throw new IllegalArgumentException("Unsupported network name: " + network);
        };
    }
}
