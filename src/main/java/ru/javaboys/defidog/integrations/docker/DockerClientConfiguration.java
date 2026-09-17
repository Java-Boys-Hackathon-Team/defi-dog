package ru.javaboys.defidog.integrations.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class DockerClientConfiguration {

    /**
     * Адрес демона Docker, в котором запускаются контейнеры статических анализаторов.
     * <p>
     * По умолчанию - сокет, проброшенный в контейнер приложения. Прежнее значение для
     * прода (tcp://host.docker.internal:2375) требовало открытого на хосте порта Docker
     * без шифрования и проверки клиента, то есть полного доступа к хосту для всей сети;
     * сокет даёт ту же возможность только этому контейнеру.
     */
    @Bean
    public DockerClient dockerClient(@Value("${docker.host:unix:///var/run/docker.sock}") String dockerHost) {
        log.info("Создание DockerClient с dockerHost={}", dockerHost);

        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
