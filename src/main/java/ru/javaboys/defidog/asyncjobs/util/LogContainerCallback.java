package ru.javaboys.defidog.asyncjobs.util;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;

public class LogContainerCallback extends LogContainerResultCallback {
    private final StringBuilder log = new StringBuilder();

    @Override
    public void onNext(Frame frame) {
        log.append(new String(frame.getPayload()));
    }

    @Override
    public String toString() {
        return log.toString();
    }
}

