package ru.javaboys.defidog.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum BlockchainNetwork implements EnumClass<String> {

    ETHEREUM("ETHEREUM"),
    BSC("BSC"),
    POLYGON("POLYGON"),
    SOLANA("SOLANA");

    private final String id;

    BlockchainNetwork(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BlockchainNetwork fromId(String id) {
        for (BlockchainNetwork at : BlockchainNetwork.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}