package com.stefano.bff_viacep.service;

import com.stefano.bff_viacep.client.ViaCepClient;
import com.stefano.bff_viacep.client.dto.ViaCepResponse;
import com.stefano.bff_viacep.dto.EnderecoReponse;
import com.stefano.bff_viacep.exception.custom.EnderecoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EnderecoService")
class EnderecoServiceTest {

    @Mock
    private ViaCepClient viaCepClient;

    private EnderecoService enderecoService;

    @BeforeEach
    void setup() {
        enderecoService = new EnderecoService(viaCepClient);
    }

    @Test
    @DisplayName("Deve buscar endereço por CEP com sucesso")
    void deveBuscarEnderecoPorCepComSucesso() {
        // Arrange
        String cep = "01001000";
        ViaCepResponse viaCepResponse = new ViaCepResponse(
                "01001-000",
                "Praça da Sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP",
                "3550308",
                "1004",
                "11",
                "7107",
                null
        );

        when(viaCepClient.buscarEnderecoPorCep(cep)).thenReturn(viaCepResponse);

        // Act
        EnderecoReponse resultado = enderecoService.buscarEnderecoPorCep(cep);

        // Assert
        assertNotNull(resultado);
        assertEquals("01001-000", resultado.cep());
        assertEquals("praça da sé", resultado.logradouro()); // convertido para lowercase
        assertEquals("lado ímpar", resultado.complemento());
        assertEquals("Sé", resultado.bairro());
        assertEquals("São Paulo", resultado.localidade());
        assertEquals("SP", resultado.uf());

        verify(viaCepClient, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CEP não é encontrado")
    void deveLancarExcecaoQuandoCepNaoEncontrado() {
        // Arrange
        String cep = "99999999";
        ViaCepResponse viaCepResponse = new ViaCepResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "true"
        );

        when(viaCepClient.buscarEnderecoPorCep(cep)).thenReturn(viaCepResponse);

        // Act & Assert
        assertThrows(EnderecoNaoEncontradoException.class,
                () -> enderecoService.buscarEnderecoPorCep(cep),
                "Deve lançar EnderecoNaoEncontradoException quando erro = true");

        verify(viaCepClient, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve converter logradouro para lowercase")
    void deveConverterLogradouroParaLowercase() {
        // Arrange
        String cep = "01001000";
        ViaCepResponse viaCepResponse = new ViaCepResponse(
                "01001-000",
                "PRAÇA DA SÉ",
                "LADO ÍMPAR",
                "SÉ",
                "SÃO PAULO",
                "SP",
                "3550308",
                "1004",
                "11",
                "7107",
                null
        );

        when(viaCepClient.buscarEnderecoPorCep(cep)).thenReturn(viaCepResponse);

        // Act
        EnderecoReponse resultado = enderecoService.buscarEnderecoPorCep(cep);

        // Assert
        assertNotNull(resultado);
        assertEquals("praça da sé", resultado.logradouro());
    }

    @Test
    @DisplayName("Deve retornar EnderecoReponse corretamente transformado")
    void deveRetornarEnderecoReponseTransformado() {
        // Arrange
        String cep = "01001000";
        ViaCepResponse viaCepResponse = new ViaCepResponse(
                "01001-000",
                "Praça da Sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP",
                "3550308",
                "1004",
                "11",
                "7107",
                null
        );

        when(viaCepClient.buscarEnderecoPorCep(cep)).thenReturn(viaCepResponse);

        // Act
        EnderecoReponse resultado = enderecoService.buscarEnderecoPorCep(cep);

        // Assert
        assertAll(
                () -> assertEquals("01001-000", resultado.cep()),
                () -> assertEquals("praça da sé", resultado.logradouro()),
                () -> assertEquals("lado ímpar", resultado.complemento()),
                () -> assertEquals("Sé", resultado.bairro()),
                () -> assertEquals("São Paulo", resultado.localidade()),
                () -> assertEquals("SP", resultado.uf())
        );
    }

    @Test
    @DisplayName("Deve chamar o ViaCepClient corretamente")
    void deveCharmarViaCepClientCorretamente() {
        // Arrange
        String cep = "01001000";
        ViaCepResponse viaCepResponse = new ViaCepResponse(
                "01001-000",
                "Praça da Sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP",
                "3550308",
                "1004",
                "11",
                "7107",
                null
        );

        when(viaCepClient.buscarEnderecoPorCep(cep)).thenReturn(viaCepResponse);

        // Act
        enderecoService.buscarEnderecoPorCep(cep);

        // Assert
        verify(viaCepClient).buscarEnderecoPorCep(cep);
        verify(viaCepClient, times(1)).buscarEnderecoPorCep(anyString());
    }
}

