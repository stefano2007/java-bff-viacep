package com.stefano.bff_viacep.dto;

import com.stefano.bff_viacep.client.dto.ViaCepResponse;

public record EnderecoReponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf
) {
    public static EnderecoReponse of(ViaCepResponse response) {
        return new EnderecoReponse(
                response.cep(),
                response.logradouro() != null ? response.logradouro().toLowerCase() : "",
                response.complemento(),
                response.bairro(),
                response.localidade(),
                response.uf()
        );
    }
}
