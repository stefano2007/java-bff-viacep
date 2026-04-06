package com.stefano.bff_viacep.client;

import com.stefano.bff_viacep.client.dto.ViaCepResponse;
import com.stefano.bff_viacep.exception.custom.EnderecoNaoEncontradoException;
import com.stefano.bff_viacep.exception.custom.EnderecoRequisicaoInvalidaException;
import com.stefano.bff_viacep.exception.custom.EnderecoErroInternoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ViaCepClient {

    private Logger logger = LoggerFactory.getLogger(ViaCepClient.class);

    private final RestClient restClient;

    public ViaCepClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        logger.info("Realizando chamada para API ViaCep: CEP {}", cep);

        return restClient.get()
                .uri("/{cep}/json/", cep)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 400) {
                        throw new EnderecoRequisicaoInvalidaException(cep);
                    }
                    throw new EnderecoNaoEncontradoException(cep);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new EnderecoErroInternoException("Erro no servidor do ViaCep");
                })
                .body(ViaCepResponse.class);
    }
}
