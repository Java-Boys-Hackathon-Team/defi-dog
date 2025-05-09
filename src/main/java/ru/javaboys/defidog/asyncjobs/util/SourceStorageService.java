package ru.javaboys.defidog.asyncjobs.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceType;

import java.nio.file.Path;

@Service
public class SourceStorageService {

    @Value("${source.storage.root:./sources}")
    private String sourceStorageRoot;

    public Path getSourceDirectory(SourceCode sourceCode) {
        SourceType type = SourceType.fromId(sourceCode.getSourceType());
        if (type == null) throw new IllegalArgumentException("Unknown SourceType: " + sourceCode.getSourceType());
        return Path.of(sourceStorageRoot, type.getId(), sourceCode.getId().toString());
    }

    public String getRelativePath(Path path) {
        Path cwd = Path.of("").toAbsolutePath(); // current working directory
        Path absolute = path.toAbsolutePath().normalize();
        return cwd.relativize(absolute).toString().replace("\\", "/"); // на случай Windows
    }
}
