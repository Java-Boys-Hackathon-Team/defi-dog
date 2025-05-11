package ru.javaboys.defidog.asyncjobs.service;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;

import io.jmix.core.UnconstrainedDataManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.javaboys.defidog.asyncjobs.dto.ScanResult;
import ru.javaboys.defidog.asyncjobs.util.LogContainerCallback;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.entity.SecurityScanJobStatus;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityScannerService {

    private final DockerClient dockerClient;
    private final UnconstrainedDataManager dataManager;

    public SourceCodeSecurityScanJob runScanTool(ScanTool scanTool, SourceCodeChangeSet changeSet) {
        SourceCode sourceCode = changeSet.getSourceCode();
        File repoDir = Path.of(sourceCode.getLocalPath()).toFile();
        String containerName = "scan-" + scanTool.getName().toLowerCase() + "-" + sourceCode.getId();

        String outputFileName = "scan-result-" + scanTool.getName().toLowerCase() + ".txt";
        File outputFile = new File(repoDir, outputFileName);

        ScanResult result;

        try {
            result = runDockerScan(scanTool, repoDir, outputFile, containerName);
        } catch (Exception e) {
            log.error("Ошибка запуска docker-контейнера: {}", e.getMessage(), e);
            result = ScanResult.builder()
                    .status(SecurityScanJobStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .build();
        }

        SourceCodeSecurityScanJob job = dataManager.create(SourceCodeSecurityScanJob.class);
        job.setSourceCodeChangeSet(changeSet);
        job.setScanTool(scanTool);
        job.setStatus(result.getStatus());
        job.setRawOutput(result.getRawOutput() != null ? result.getRawOutput() : result.getErrorMessage());
        job.setExecutionLog(result.getExecutionLog());
        job.setCreatedDate(OffsetDateTime.now());

        scanTool.setSourceCodeSecurityScanJob(job);

        return job;
    }

    @SneakyThrows
    private ScanResult runDockerScan(ScanTool scanTool, File repoDir, File outputFile, String containerName) {
        try {
            String image = scanTool.getDockerImage();

            String[] cmdParams = scanTool.getContainerCmdParams() != null
                    ? scanTool.getContainerCmdParams().split("\\s+")
                    : new String[]{"."}; // по умолчанию — путь к коду

            log.info("Создание контейнера: image={}, params={}, mount={}", image, List.of(cmdParams), repoDir.getAbsolutePath());

            Bind mount = new Bind(
                    repoDir.getAbsolutePath(),
                    new Volume("/repo")  // внутри контейнера будет /repo
            );

            CreateContainerResponse container = dockerClient.createContainerCmd(image)
                    .withCmd(cmdParams) // <-- параметры из ScanTool
                    .withName(containerName + "-" + System.currentTimeMillis())
                    .withHostConfig(HostConfig.newHostConfig().withBinds(mount))
                    .withWorkingDir("/repo")
                    .exec();

            String containerId = container.getId();

            dockerClient.startContainerCmd(containerId).exec();

            int maxWaitSec = 120;
            int waited = 0;
            while (Boolean.TRUE.equals(dockerClient.inspectContainerCmd(containerId).exec().getState().getRunning()) && waited < maxWaitSec) {
                Thread.sleep(1000);
                waited++;
            }

            // Проверка завершения
            var inspect = dockerClient.inspectContainerCmd(containerId).exec();
            boolean exited = Boolean.FALSE.equals(inspect.getState().getRunning());
            Integer exitCode = inspect.getState().getExitCode();

            String logs = dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new LogContainerCallback())
                    .awaitCompletion()
                    .toString();

            String executionLog = """
                    [SCAN CONTEXT]
                    Docker Image: %s
                    Container Name: %s
                    Mount path: %s
                    Working Dir (host): %s
                    Working Dir (container): /repo
                    Container Cmd Params: %s
                    
                    [DOCKER EQUIVALENT]
                    docker run --rm -v %s:/repo -w /repo %s %s
                    
                    [RESULT]
                    Exit Code: %s
                    """.formatted(
                    image,
                    containerName,
                    mount.getPath(),
                    repoDir.getAbsolutePath(),
                    String.join(" ", cmdParams),
                    repoDir.getAbsolutePath(),
                    image,
                    String.join(" ", cmdParams),
                    exitCode
            );

            dockerClient.removeContainerCmd(containerId).withForce(true).exec();

            if (!exited || exitCode != 0) {
                return ScanResult.builder()
                        .status(SecurityScanJobStatus.FAILED)
                        .exitCode(exitCode)
                        .errorMessage("Контейнер завершился с кодом " + exitCode + ": " + logs)
                        .rawOutput(logs)
                        .executionLog(executionLog)
                        .build();
            }

            if (!outputFile.exists()) {
                return ScanResult.builder()
                        .status(SecurityScanJobStatus.FAILED)
                        .exitCode(exitCode)
                        .errorMessage("Файл с результатом не найден: " + outputFile.getAbsolutePath())
                        .rawOutput(logs)
                        .executionLog(executionLog)
                        .build();
            }

            String raw = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);

            return ScanResult.builder()
                    .status(SecurityScanJobStatus.COMPLETED)
                    .rawOutput(raw)
                    .executionLog(executionLog)
                    .build();

        } catch (InterruptedException | RuntimeException e) {
            return ScanResult.builder()
                    .status(SecurityScanJobStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    private String captureContainerLogs(String containerId) {
        try {
            LogContainerCallback callback = new LogContainerCallback();
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(callback)
                    .awaitCompletion();
            return callback.toString();
        } catch (Exception e) {
            log.warn("Ошибка при получении логов контейнера {}: {}", containerId, e.getMessage());
            return "<не удалось получить логи>";
        }
    }

    private String findFirstSolidityFile(File repoDir) {
        try (Stream<Path> files = Files.walk(repoDir.toPath())) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".sol"))
                    .map(p -> repoDir.toPath().relativize(p).toString())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Не найден ни один .sol-файл"));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка поиска .sol-файлов", e);
        }
    }

    private String buildMythrilCommandParam(File repoDir) {
        try (Stream<Path> files = Files.walk(repoDir.toPath())) {
            List<String> relPaths = files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".sol"))
                    .map(p -> repoDir.toPath().relativize(p).toString())
                    .collect(Collectors.toList());

            if (relPaths.isEmpty()) {
                throw new IllegalStateException("В репозитории не найдено .sol-файлов");
            }

            String inputFiles = String.join(" ", relPaths);
            return "%s -o json -j /repo/scan-result-mythril.txt".formatted(inputFiles);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка поиска .sol-файлов", e);
        }
    }


}
