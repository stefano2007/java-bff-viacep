package com.stefano.bff_viacep.testeIntegrados.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

import jakarta.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class EmbeddedRedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedRedisConfig.class);
    
    private RedisServer redisServer;

    @Bean
    public RedisServer redisServer() throws IOException {
        logger.info("=== Iniciando Embedded Redis ===");
        
        redisServer = new RedisServer(9379);
        redisServer.start();
        
        logger.info("✅ Embedded Redis iniciado na porta 9379");
        return redisServer;
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            logger.info("Parando Embedded Redis");
            try {
                redisServer.stop();
                logger.info("✅ Embedded Redis parado");
            } catch (Exception e) {
                logger.error("Erro ao parar Redis: {}", e.getMessage());
            }
        }
    }
}


