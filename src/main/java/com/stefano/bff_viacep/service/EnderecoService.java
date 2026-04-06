package com.stefano.bff_viacep.service;

import com.stefano.bff_viacep.client.ViaCepClient;
import com.stefano.bff_viacep.dto.EnderecoReponse;
import com.stefano.bff_viacep.exception.custom.EnderecoNaoEncontradoException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    private final ViaCepClient viaCepClient;

    public EnderecoService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    @Cacheable(value = "enderecos", key = "#cep")
    public EnderecoReponse buscarEnderecoPorCep(String cep) {
        var response = viaCepClient.buscarEnderecoPorCep(cep);

        if (response.ehErro()) {
            throw new EnderecoNaoEncontradoException(cep);
        }

        return EnderecoReponse.of(response);
    }
}
