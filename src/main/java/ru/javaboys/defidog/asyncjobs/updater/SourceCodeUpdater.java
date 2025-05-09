package ru.javaboys.defidog.asyncjobs.updater;

import ru.javaboys.defidog.entity.SourceCode;

public interface SourceCodeUpdater {
    /**
     * @param sourceCode сущность для обновления
     * @return лог или результат обработки
     */
    String update(SourceCode sourceCode) throws Exception;
}
