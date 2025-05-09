package ru.javaboys.defidog.crypto;

import org.springframework.context.ApplicationEvent;

public class CryptocurrencyReloadEvent extends ApplicationEvent {
    public CryptocurrencyReloadEvent(Object source) {
        super(source);
    }
}
