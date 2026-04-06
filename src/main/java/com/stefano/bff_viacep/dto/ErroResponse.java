package com.stefano.bff_viacep.dto;

import java.time.LocalDateTime;

public record ErroResponse (
        LocalDateTime timestamp,
        int status,
        String mensagem,
        String path
) {

    public static ErroResponse criarResponse(int statusCode, String mensagem, String path) {
        return new ErroResponse(
                LocalDateTime.now(),
                statusCode,
                mensagem,
                path
        );
    }
}
