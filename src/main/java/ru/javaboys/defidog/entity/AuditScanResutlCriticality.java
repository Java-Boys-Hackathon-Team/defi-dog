package ru.javaboys.defidog.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum AuditScanResutlCriticality implements EnumClass<String> {

    CRITICAL("CRITICAL"),
    HIGH("HIGH"),
    NORMAL("NORMAL");

    private final String id;

    AuditScanResutlCriticality(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AuditScanResutlCriticality fromId(String id) {
        for (AuditScanResutlCriticality at : AuditScanResutlCriticality.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}