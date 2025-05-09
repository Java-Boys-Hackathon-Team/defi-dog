package ru.javaboys.defidog.asyncjobs.updater;

import ru.javaboys.defidog.entity.SourceType;

public interface TypedUpdater {
    SourceType getSupportedSourceType();
}
