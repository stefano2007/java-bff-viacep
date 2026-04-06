package com.stefano.bff_viacep.exception.custom;

public class EnderecoNaoEncontradoException extends RuntimeException {

    public EnderecoNaoEncontradoException(String cep) {
        super("Endereço não encontrado para o CEP: %s".formatted(cep));
    }
}
