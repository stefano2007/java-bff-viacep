package com.stefano.bff_viacep.testeIntegrados.config;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestConfiguration
public class WireMockConfig {

    private static final Logger logger = LoggerFactory.getLogger(WireMockConfig.class);

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        logger.info("=== Iniciando WireMock ===");

        WireMockServer server = new WireMockServer(
                options()
                        .port(8089)
                        .notifier(new ConsoleNotifier(true))
                        .usingFilesUnderClasspath("__files")
        );

        logger.info("✅ WireMock inicializado na porta 8089");
        return server;
    }
}
