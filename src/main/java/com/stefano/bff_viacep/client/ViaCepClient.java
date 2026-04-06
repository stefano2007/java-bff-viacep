package com.stefano.bff_viacep.client;

import com.stefano.bff_viacep.client.dto.ViaCepResponse;
import com.stefano.bff_viacep.exception.custom.EnderecoErroTempoEsperaException;
import com.stefano.bff_viacep.exception.custom.EnderecoNaoEncontradoException;
import com.stefano.bff_viacep.exception.custom.EnderecoRequisicaoInvalidaException;
import com.stefano.bff_viacep.exception.custom.EnderecoErroInternoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class ViaCepClient {

    private final Logger logger = LoggerFactory.getLogger(ViaCepClient.class);

    private final RestClient restClient;

    public ViaCepClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        logger.info("Realizando chamada para API ViaCep: CEP {}", cep);
        try {
            ViaCepResponse response = realizarChamadaViaCep(cep);
            logger.info("Resposta recebida do ViaCep: {}", response);
            return response;
        } catch (ResourceAccessException ex) {
            logger.error("Erro de conexão com ViaCep: {}", ex.getMessage());
            throw new EnderecoErroTempoEsperaException("Erro de tempo de espera ao acessar ViaCep");
        }
    }

    private ViaCepResponse realizarChamadaViaCep(String cep) {
        return restClient.get()
                .uri("/{cep}/json/", cep)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 400) {
                        logger.warn("ViaCep - CEP mal formado : {}", cep);
                        throw new EnderecoRequisicaoInvalidaException(cep);
                    }
                    logger.warn("ViaCep - Status {}, CEP {}", response.getStatusCode(), cep);
                    throw new EnderecoNaoEncontradoException(cep);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    logger.error("Erro Interno no servidor do ViaCep: Status {}", response.getStatusCode());
                    throw new EnderecoErroInternoException("Erro Interno no servidor do ViaCep");
                })
                .body(ViaCepResponse.class);
    }
}
