package ru.javaboys.defidog.entity;

import org.springframework.lang.Nullable;

import io.jmix.core.metamodel.datatype.EnumClass;

public enum ChannelEnum implements EnumClass<String> {

    EMAIL("EMAIL"),
    TELEGRAM("TELEGRAM");

    private final String id;

    ChannelEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ChannelEnum fromId(String id) {
        for (ChannelEnum at : ChannelEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}