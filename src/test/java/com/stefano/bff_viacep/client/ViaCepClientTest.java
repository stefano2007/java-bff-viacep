package com.stefano.bff_viacep.client;

import com.stefano.bff_viacep.client.dto.ViaCepResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Testes Unitários - ViaCepClient")
@ActiveProfiles("test")
class ViaCepClientTest {

    @Autowired
    private ViaCepClient viaCepClient;

    @Test
    @DisplayName("Deve buscar CEP com sucesso usando o WireMock")
    void deveBuscarCepComSucesso() {
        // Arrange
        String cep = "01001000";

        // Act
        ViaCepResponse result = viaCepClient.buscarEnderecoPorCep(cep);

        // Assert
        assertNotNull(result);
        assertEquals("01001-000", result.cep());
        assertEquals("Praça da Sé - Testando 123", result.logradouro());
        assertEquals("SP", result.uf());
        assertFalse(result.ehErro());
    }

    @Test
    @DisplayName("Deve retornar resposta com erro = true")
    void deveRetornarRespostaComErroTrue() {
        // Arrange
        String cep = "99999999";

        // Act
        ViaCepResponse result = viaCepClient.buscarEnderecoPorCep(cep);

        // Assert
        assertNotNull(result);
        assertTrue(result.ehErro());
    }

    @Test
    @DisplayName("Deve conter os campos corretos no ViaCepResponse")
    void deveConterOsCamposCorretos() {
        // Arrange
        String cep = "01001000";

        // Act
        ViaCepResponse result = viaCepClient.buscarEnderecoPorCep(cep);

        // Assert
        assertAll(
                () -> assertNotNull(result.cep()),
                () -> assertNotNull(result.logradouro()),
                () -> assertNotNull(result.uf()),
                () -> assertNotNull(result.bairro()),
                () -> assertNotNull(result.localidade())
        );
    }
}

