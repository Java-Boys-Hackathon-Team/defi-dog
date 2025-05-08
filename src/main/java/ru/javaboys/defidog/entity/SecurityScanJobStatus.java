package ru.javaboys.defidog.entity;

import io.jmix.core.metamodel.datatype.EnumClass;
import org.springframework.lang.Nullable;


public enum SecurityScanJobStatus implements EnumClass<String> {

    QUEUED("QUEUED"),
    RUNNING("RUNNING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private final String id;

    SecurityScanJobStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SecurityScanJobStatus fromId(String id) {
        for (SecurityScanJobStatus at : SecurityScanJobStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}