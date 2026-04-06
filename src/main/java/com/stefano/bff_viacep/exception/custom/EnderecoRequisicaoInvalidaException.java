package com.stefano.bff_viacep.exception.custom;

public class EnderecoRequisicaoInvalidaException extends RuntimeException {
    public EnderecoRequisicaoInvalidaException(String cep) {
        super("Requisição invalida para o CEP: %s".formatted(cep));
    }
}
