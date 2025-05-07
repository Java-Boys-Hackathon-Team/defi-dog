package ru.javaboys.defidog.integrations.dedaub;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.javaboys.defidog.integrations.dedaub.dto.DecompilationDto;

import java.time.Duration;
import java.time.Instant;

// https://docs.dedaub.com/docs/api/nested
@Service
@Slf4j
public class DedaubServiceImpl implements DedaubService {

    private final RestClient dedaubClient;

    public DedaubServiceImpl(@Qualifier("dedaubClient") RestClient dedaubClient) {
        this.dedaubClient = dedaubClient;
    }

    @Override
    @SneakyThrows
    public DecompilationDto decompileSmartContractBytecode(String bytecode, Duration timeout, long sleepInterval) {
        String md5 = submitBytecode(bytecode);
        waitForCompletion(md5, timeout, sleepInterval);
        return fetchDecompilation(md5);
    }

    private String submitBytecode(String bytecode) throws JsonProcessingException {
        log.info("Submitting bytecode for decompilation");
        var resp = dedaubClient.post()
                .uri("/api/on_demand")
                .body(bytecode)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return resp.replaceAll("^\"|\"$", "");
    }

    private void waitForCompletion(String md5, Duration timeout, long sleepInterval) {
        log.info("Waiting for decompilation to complete for {}", md5);

        Instant deadline = Instant.now().plus(timeout);

        while (Instant.now().isBefore(deadline)) {
            String stage = dedaubClient.get()
                    .uri("/api/on_demand/{md5}/status", md5)
                    .retrieve()
                    .body(String.class);

            log.info("Decompilation status for {}: {}", md5, stage);
            stage = stage.replaceAll("^\"|\"$", "");
            if ("COMPLETED".equals(stage)) {
                return;
            }

            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while polling decompiler status", e);
            }
        }

        throw new RuntimeException("Timeout waiting for decompilation to complete for " + md5);
    }

    private DecompilationDto fetchDecompilation(String md5) {
        log.info("Fetching decompiled code for {}", md5);
        return dedaubClient.get()
                .uri("/api/on_demand/decompilation/{md5}", md5)
                .retrieve()
                .body(DecompilationDto.class);
    }
}
