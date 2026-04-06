package com.stefano.bff_viacep.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation .Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${viacep.url}")
    private String baseUrl;

    @Bean
    public RestClient restClientViaCEP(RestClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .requestFactory(getClientRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory getClientRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return factory;
    }
}
