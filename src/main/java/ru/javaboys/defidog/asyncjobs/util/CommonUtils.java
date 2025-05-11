package ru.javaboys.defidog.asyncjobs.util;

import ru.javaboys.defidog.entity.BlockchainNetwork;

public class CommonUtils {
    public static String getChainIdByNetworkName(BlockchainNetwork network) {
        return switch (network) {
            case BlockchainNetwork.ETHEREUM -> "1";
            case BlockchainNetwork.POLYGON -> "137";
            case BlockchainNetwork.BSC -> "56";
            case BlockchainNetwork.SOLANA-> "101";
            default -> throw new IllegalArgumentException("Unsupported network name: " + network);
        };
    }
}
