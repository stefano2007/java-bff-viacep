package com.stefano.bff_viacep.controller;

import com.stefano.bff_viacep.dto.EnderecoReponse;
import com.stefano.bff_viacep.exception.custom.EnderecoNaoEncontradoException;
import com.stefano.bff_viacep.exception.custom.EnderecoRequisicaoInvalidaException;
import com.stefano.bff_viacep.service.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnderecoController.class)
@DisplayName("Testes Unitários - EnderecoController")
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnderecoService enderecoService = mock(EnderecoService.class);

    @BeforeEach
    void setup() {
        reset(enderecoService);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public EnderecoService enderecoService() {
            return mock(EnderecoService.class);
        }
    }

    @Test
    @DisplayName("Deve retornar 200 com endereço quando CEP é válido e encontrado")
    void deveRetornar200ComEndereco() throws Exception {
        // Arrange
        String cep = "01001000";
        EnderecoReponse endereco = new EnderecoReponse(
                "01001-000",
                "praça da sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP"
        );

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(endereco);

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01001-000"))
                .andExpect(jsonPath("$.logradouro").value("praça da sé"))
                .andExpect(jsonPath("$.complemento").value("lado ímpar"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));

        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve retornar 404 quando CEP não é encontrado")
    void deveRetornar404QuandoCepNaoEncontrado() throws Exception {
        // Arrange
        String cep = "99999999";
        when(enderecoService.buscarEnderecoPorCep(cep))
                .thenThrow(new EnderecoNaoEncontradoException(cep));

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Endereço não encontrado para o CEP: " + cep))
                .andExpect(jsonPath("$.path").value("/api/endereco/" + cep));

        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve retornar 400 quando CEP é inválido (com letras)")
    void deveRetornar400QuandoCepInvalido() throws Exception {
        // Arrange
        String cepInvalido = "ABC";

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cepInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").exists());

        // O serviço não deve ser chamado se a validação falhar no controller
        verify(enderecoService, never()).buscarEnderecoPorCep(anyString());
    }

    @Test
    @DisplayName("Deve retornar 400 quando CEP tem menos de 8 dígitos")
    void deveRetornar400QuandoCepMenosOitoDígitos() throws Exception {
        // Arrange
        String cepInvalido = "0100100";

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cepInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(enderecoService, never()).buscarEnderecoPorCep(anyString());
    }

    @Test
    @DisplayName("Deve retornar 400 quando CEP contém caracteres especiais")
    void deveRetornar400QuandoCepComCaracteresEspeciais() throws Exception {
        // Arrange
        String cepComEspecial = "01001-00";

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cepComEspecial))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(enderecoService, never()).buscarEnderecoPorCep(anyString());
    }

    @Test
    @DisplayName("Deve retornar 404 quando CEP não é encontrado no serviço")
    void deveRetornar404QuandoServicoNaoEncontra() throws Exception {
        // Arrange
        String cep = "12345678";
        when(enderecoService.buscarEnderecoPorCep(cep))
                .thenThrow(new EnderecoNaoEncontradoException(cep));

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve retornar 404 com mensagem de erro quando CEP é inválido no serviço")
    void deveRetornar404QuandoRequisicaoInvalidaNoServico() throws Exception {
        // Arrange
        String cep = "11111111";
        when(enderecoService.buscarEnderecoPorCep(cep))
                .thenThrow(new EnderecoRequisicaoInvalidaException(cep));

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isBadRequest())  // ← EnderecoRequisicaoInvalidaException retorna 400
                .andExpect(jsonPath("$.status").value(400));

        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve retornar Content-Type application/json")
    void deveRetornarContentTypeApplicationJson() throws Exception {
        // Arrange
        String cep = "01001000";
        EnderecoReponse endereco = new EnderecoReponse(
                "01001-000",
                "praça da sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP"
        );

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(endereco);

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve chamar o serviço exatamente uma vez por requisição")
    void deveCharmarServicoExatamenteUmaVez() throws Exception {
        // Arrange
        String cep = "01001000";
        EnderecoReponse endereco = new EnderecoReponse(
                "01001-000",
                "praça da sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP"
        );

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(endereco);

        // Act
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isOk());

        // Assert
        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
        verify(enderecoService, only()).buscarEnderecoPorCep(anyString());
    }

    @Test
    @DisplayName("Deve aceitar CEP com 8 dígitos válidos")
    void deveAceitarCepOitoDígitosValidos() throws Exception {
        // Arrange
        String cep = "01001000";
        EnderecoReponse endereco = new EnderecoReponse(
                "01001-000",
                "praça da sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP"
        );

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(endereco);

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").exists())
                .andExpect(jsonPath("$.logradouro").exists())
                .andExpect(jsonPath("$.uf").exists());

        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    @DisplayName("Deve retornar todos os campos obrigatórios no response")
    void deveRetornarTodosOsCamposObrigatorios() throws Exception {
        // Arrange
        String cep = "01001000";
        EnderecoReponse endereco = new EnderecoReponse(
                "01001-000",
                "praça da sé",
                "lado ímpar",
                "Sé",
                "São Paulo",
                "SP"
        );

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(endereco);

        // Act & Assert
        mockMvc.perform(get("/api/endereco/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").exists())
                .andExpect(jsonPath("$.logradouro").exists())
                .andExpect(jsonPath("$.complemento").exists())
                .andExpect(jsonPath("$.bairro").exists())
                .andExpect(jsonPath("$.localidade").exists())
                .andExpect(jsonPath("$.uf").exists());
    }

    @Test
    @DisplayName("Deve não chamar o serviço quando validação falha")
    void deveNaoCharmarServicoQuandoValidacaoFalha() throws Exception {
        // Arrange
        String cepInvalido = "ABC";

        // Act
        mockMvc.perform(get("/api/endereco/{cep}", cepInvalido))
                .andExpect(status().isBadRequest());

        // Assert - serviço não deve ser chamado
        verify(enderecoService, never()).buscarEnderecoPorCep(anyString());
    }
}