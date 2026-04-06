package com.stefano.bff_viacep;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.stefano.bff_viacep.testeIntegrados.config.WireMockConfig;
import com.stefano.bff_viacep.testeIntegrados.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = { BffViacepApplication.class }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({WireMockConfig.class, EmbeddedRedisConfig.class})
class EnderecoTesteIntegrado {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoTesteIntegrado.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setup() {
        logger.info("\n========== WIREMOCK DEBUG ==========");
        logger.info("Status: ✅ ATIVO");
        logger.info("Porta: 8089");
        var mappings = wireMockServer.listAllStubMappings();
        logger.info("Mapeamentos carregados: {}", mappings.getMappings().size());
        mappings.getMappings().forEach(m ->
                logger.info("  ✓ {} {}", m.getRequest().getMethod(), m.getRequest().getUrl())
        );
        logger.info("====================================\n");
    }


    @Test
    void deveBuscarClientePorCepComSucesso() throws Exception {
        
        mockMvc.perform(get("/api/endereco/01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01001-000"))
                .andExpect(jsonPath("$.logradouro").value("praça da sé - testando 123"))
                .andExpect(jsonPath("$.complemento").value("lado ímpar"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));

        String valor = redisTemplate.opsForValue().get("enderecos::01001000"); // a chave depende da sua implementação
        Assertions.assertNotNull(valor, "O valor deve ter sido registrado no Redis");
        Assertions.assertEquals("{\"@class\":\"com.stefano.bff_viacep.dto.EnderecoReponse\",\"cep\":\"01001-000\"" +
                        ",\"logradouro\":\"praça da sé - testando 123\",\"complemento\":\"lado ímpar\",\"bairro\":\"Sé\"," +
                        "\"localidade\":\"São Paulo\",\"uf\":\"SP\"}",
                valor,  "O valor deve conter o CEP");
    }

    @Test
    void deveLancarExcecaoAoNaoEncontrarCep() throws Exception {
        mockMvc.perform(get("/api/endereco/99999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Endereço não encontrado para o CEP: 99999999"))
                .andExpect(jsonPath("$.path").value("/api/endereco/99999999"));
    }

    @Test
    void deveLancarExcecaoComCepInvalido() throws Exception {
        mockMvc.perform(get("/api/endereco/ABC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("buscarEnderecoPorCep.cep: CEP inválido. Deve conter 8 numeros."))
                .andExpect(jsonPath("$.path").value("/api/endereco/ABC"));
    }


    @Test
    void deveLancarExcecaoQuandoAPIIndiponivel() throws Exception {
        mockMvc.perform(get("/api/endereco/00000000"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensagem").value("Erro no servidor do ViaCep"))
                .andExpect(jsonPath("$.path").value("/api/endereco/00000000"));
    }
}



