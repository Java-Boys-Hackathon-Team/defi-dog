package ru.javaboys.defidog.asyncjobs.util;

public class CommonUtils {
    public static String getChainIdByNetworkName(String networkName) {
        return switch (networkName) {
            case "ETHEREUM" -> "1";
            case "POLYGON" -> "137";
            case "BSC" -> "56";
            case "SOLANA"-> "101";
            default -> throw new IllegalArgumentException("Unsupported network name: " + networkName);
        };
    }
}
