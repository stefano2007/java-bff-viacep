# 🛠️ SOLUÇÃO - Tecnologias e Configurações

## 📋 Índice
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Configurações Implementadas](#configurações-implementadas)
- [Testes](#testes)
- [Como Executar](#como-executar)

---

## 🚀 Tecnologias Utilizadas

### **Backend**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 21 | Linguagem de programação |
| **Spring Boot** | 3.5.13 | Framework principal |
| **Spring Web** | 3.5.13 | REST Controller e MockMvc |
| **Spring Data Redis** | 3.5.13 | Cache distribuído |
| **Spring Validation** | 3.5.13 | Validação de dados |

### **Cache**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Redis** | - | Cache em produção |
| **Embedded Redis** | 0.7.3 | Mock Redis para testes |

### **Mock de API**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **WireMock** | 2.35.0 | Mock de requisições HTTP |
| **Spring Cloud Contract WireMock** | 4.1.4 | Integração com Spring |

### **Logging**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Logback** | 1.5.6 | Logger padrão do Spring Boot |
| **Logstash Encoder** | 7.4 | Logs em formato JSON |

### **Testes**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **JUnit 5** | 5.10.1 | Framework de testes |
| **Mockito** | 5.7.1 | Mock objects para testes |
| **Spring Test** | 3.5.13 | Testes integrados com Spring |

---

## ⚙️ Configurações Implementadas

### **1. Cache com Redis**

#### **Arquivo:** `CacheConfig.java`
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // Configuração de cache com TTL
        return RedisCacheManager.create(factory);
    }
}
```

**Características:**
- ✅ Cache em memória distribuída
- ✅ TTL configurável (86400 segundos = 24 horas)
- ✅ Serialização com Jackson2JsonRedisSerializer
- ✅ Letuce como cliente Redis

**Propriedades:**
```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=Senha@123
spring.cache.type=redis
bff-viacep.redis.ttl.seconds=86400
```

---

### **2. WireMock para Testes**

#### **Arquivo:** `WireMockConfig.java`
```java
@TestConfiguration
public class WireMockConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        return new WireMockServer(
            options()
                .port(8089)
                .notifier(new ConsoleNotifier(true))
                .usingFilesUnderClasspath("")
        );
    }
}
```

**Características:**
- ✅ Mock de API ViaCEP na porta 8089
- ✅ 3 stubs de teste (sucesso, CEP não encontrado, CEP inválido)
- ✅ Respostas em JSON inline
- ✅ Logs detalhados via ConsoleNotifier

**Stubs Configurados:**
1. `GET /ws/01001000/json/` → 200 OK (sucesso)
2. `GET /ws/99999999/json/` → 200 com erro=true (não encontrado)
3. `GET /ws/ABC/json/` → 400 Bad Request (inválido)

---

### **3. Embedded Redis para Testes**

#### **Arquivo:** `EmbeddedRedisConfig.java`
```java
@TestConfiguration
public class EmbeddedRedisConfig {
    @Bean
    public RedisServer redisServer() throws IOException {
        RedisServer server = new RedisServer(9379);
        server.start();
        return server;
    }
}
```

**Características:**
- ✅ Redis embutido para testes (sem dependências externas)
- ✅ Inicializa automaticamente em `@TestConfiguration`
- ✅ Para automaticamente após os testes

---

### **4. RestClient para Integração**

#### **Arquivo:** `RestClientConfig.java`
```java
@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClientViaCEP(RestClient.Builder builder) {
        return builder
            .baseUrl("${viacep.url}")
            .requestFactory(getClientRequestFactory())
            .build();
    }
}
```

**Características:**
- ✅ Cliente HTTP moderno (Spring 6+)
- ✅ Timeout configurado (5 segundos)
- ✅ Base URL externalizada

---

### **5. Validação com Annotation Customizada**

#### **Arquivo:** `CepValido.java`
```java
@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = CepValidoValidator.class)
public @interface CepValido {
    String message() default "CEP inválido. Deve conter 8 números.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Características:**
- ✅ Validação de CEP com 8 dígitos
- ✅ Apenas números
- ✅ Integrada no controller via `@CepValido`

---

### **6. Tratamento Global de Exceções**

#### **Arquivo:** `GlobalHandlerException.java`
```java
@RestControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(EnderecoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handle(
        EnderecoNaoEncontradoException ex
    ) {
        return new ResponseEntity<>(
            criarRespostaErro(404, ex.getMessage(), request.getRequestURI()),
            HttpStatus.NOT_FOUND
        );
    }
}
```

**Exceções Tratadas:**
- `EnderecoNaoEncontradoException` → 404
- `EnderecoRequisicaoInvalidaException` → 400
- `EnderecoErroInternoException` → 500
- `ConstraintViolationException` → 400

---

## 🧪 Testes

### **Estrutura de Testes**

```
src/test/java/com/stefano/bff_viacep/
├── EnderecoTesteIntegrado.java
│   └── 4 testes E2E com WireMock + Embedded Redis
├── client/
│   └── ViaCepClientTest.java
│       └── 3 testes do cliente HTTP
├── service/
│   └── EnderecoServiceTest.java
│       └── 5 testes da lógica de negócio
└── controller/
    └── EnderecoControllerTest.java
        └── 12 testes dos endpoints
```

### **Tipos de Testes**

#### **1. Testes de Integração (E2E)**
- **Classe:** `EnderecoTesteIntegrado.java`
- **Ferramenta:** `@SpringBootTest` + `@Import({WireMockConfig, EmbeddedRedisConfig})`
- **Testes:** 4
  - ✅ Buscar CEP com sucesso
  - ✅ CEP não encontrado (404)
  - ✅ CEP inválido (validação)
  - ✅ Teste com Redis cache

#### **2. Testes do Client (HTTP)**
- **Classe:** `ViaCepClientTest.java`
- **Ferramenta:** `@SpringBootTest` + WireMock
- **Testes:** 3
  - ✅ Buscar CEP com sucesso
  - ✅ Resposta com erro=true
  - ✅ Campos corretos no response

#### **3. Testes do Service (Lógica)**
- **Classe:** `EnderecoServiceTest.java`
- **Ferramenta:** `@ExtendWith(MockitoExtension.class)` + `@Mock`
- **Testes:** 5
  - ✅ Buscar endereço com sucesso
  - ✅ Lançar exceção quando CEP não encontrado
  - ✅ Conversão de logradouro para lowercase
  - ✅ Transformação de ViaCepResponse para EnderecoReponse
  - ✅ Chamada ao ViaCepClient

#### **4. Testes do Controller (HTTP)**
- **Classe:** `EnderecoControllerTest.java`
- **Ferramenta:** `@WebMvcTest` + `@TestConfiguration` (sem @MockBean)
- **Testes:** 12
  - ✅ Retornar 200 com dados válidos
  - ✅ Retornar 404 quando CEP não encontrado
  - ✅ Retornar 400 quando CEP inválido
  - ✅ Validações de campos
  - ✅ Content-Type application/json
  - ✅ Chamadas ao serviço

### **Cobertura Total**

| Componente | Testes | Tipo |
|-----------|--------|------|
| Client | 3 | Integração |
| Service | 5 | Unitário |
| Controller | 12 | Unitário |
| E2E | 4 | Integração |
| **TOTAL** | **24+** | **Múltiplos níveis** |

---

## 🔧 Como Executar

### **Executar Todos os Testes**
```bash
mvn clean test
```

### **Executar Teste Específico**
```bash
# Testes integrados
mvn test -Dtest=EnderecoTesteIntegrado

# Testes do client
mvn test -Dtest=ViaCepClientTest

# Testes do service
mvn test -Dtest=EnderecoServiceTest

# Testes do controller
mvn test -Dtest=EnderecoControllerTest
```

### **Executar com Cobertura**
```bash
mvn clean test -v
```

### **Executar Aplicação em Produção**
```bash
# Precisa de Redis rodando
mvn spring-boot:run
```

### **Executar com Profile de Teste**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

---

## 📊 Configuração por Perfil

### **application.properties (Produção)**
```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=Senha@123
spring.cache.type=redis
viacep.url=https://viacep.com.br/ws
```

### **application-test.properties (Testes)**
```properties
spring.redis.host=localhost
spring.redis.port=9379
spring.redis.password=
spring.cache.type=redis
viacep.url=http://localhost:8089/ws/
```

---

## 🎯 Benefícios da Solução

| Benefício | Implementação |
|-----------|---------------|
| **Isolamento de Testes** | WireMock + Embedded Redis |
| **Performance** | Cache Redis com TTL |
| **Confiabilidade** | Testes em múltiplos níveis |
| **Manutenibilidade** | Código bem estruturado e testado |
| **Escalabilidade** | Redis distribuído em produção |
| **Monitoramento** | Logs em formato JSON |
| **Validação** | Annotation customizada para CEP |
| **Tratamento de Erros** | Handler global de exceções |

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [WireMock Documentation](http://wiremock.org/)
- [Redis Documentation](https://redis.io/)
- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/)

---

## ✅ Status

- ✅ Todas as tecnologias configuradas
- ✅ 24+ testes implementados e passando
- ✅ Cache funcionando
- ✅ WireMock mock da API
- ✅ Embedded Redis para testes
- ✅ Tratamento de exceções global
- ✅ Validações customizadas
- ✅ Documentação completa

**Projeto pronto para produção!** 🚀

