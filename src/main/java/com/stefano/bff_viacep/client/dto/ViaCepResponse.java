package com.stefano.bff_viacep.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        String erro
) implements Serializable {
    public boolean ehErro() {
        return Boolean.parseBoolean(erro);
    }
}
