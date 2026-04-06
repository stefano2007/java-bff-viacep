package com.stefano.bff_viacep.exception;

import com.stefano.bff_viacep.dto.ErroResponse;
import com.stefano.bff_viacep.exception.custom.EnderecoErroInternoException;
import com.stefano.bff_viacep.exception.custom.EnderecoNaoEncontradoException;
import com.stefano.bff_viacep.exception.custom.EnderecoRequisicaoInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalHandlerException {

    private final Logger logger = LoggerFactory.getLogger(GlobalHandlerException.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> handle(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String erro = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Erro de validação");

        logger.warn("Erro de validação: {}", ex.getMessage());

        return new ResponseEntity<>(
                criarRespostaErro(HttpStatus.BAD_REQUEST.value(), erro, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EnderecoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handle(
            EnderecoNaoEncontradoException ex,
            HttpServletRequest request
    ) {
        logger.warn("Erro de validação: {}", ex.getMessage());
        return new ResponseEntity<>(
                criarRespostaErro(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(EnderecoRequisicaoInvalidaException.class)
    public ResponseEntity<ErroResponse> handle(
            EnderecoRequisicaoInvalidaException ex,
            HttpServletRequest request
    ) {
        logger.warn("Erro de validação: {}", ex.getMessage());
        return new ResponseEntity<>(
                criarRespostaErro(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EnderecoErroInternoException.class)
    public ResponseEntity<ErroResponse> handle(
            EnderecoErroInternoException ex,
            HttpServletRequest request
    ) {
        logger.warn("Erro Interno: {}", ex.getMessage());
        return new ResponseEntity<>(
                criarRespostaErro(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ErroResponse criarRespostaErro(int statusCode, String mensagem, String path) {
        return ErroResponse.criarResponse(
                statusCode,
                mensagem,
                path
        );
    }
}
