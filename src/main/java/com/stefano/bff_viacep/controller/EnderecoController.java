package com.stefano.bff_viacep.controller;

import com.stefano.bff_viacep.dto.EnderecoReponse;
import com.stefano.bff_viacep.service.EnderecoService;
import com.stefano.bff_viacep.validation.annotation.CepValido;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/api/endereco/{cep}")
    public ResponseEntity<EnderecoReponse> buscarEnderecoPorCep(
            @PathVariable("cep") @CepValido String cep
    ) {
        var endereco = enderecoService.buscarEnderecoPorCep(cep);
        return ResponseEntity.ok(endereco);
    }
}
