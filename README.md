# 📦 BFF - Consulta de Endereço (ViaCEP)

## 📖 Descrição

Aplicação web simples que consome a API pública ViaCEP para consultar informações de endereço a partir de um CEP.

A aplicação atua como uma API intermediária (BFF), responsável por:
- Buscar os dados na API externa
- Tratar as informações
- Retornar apenas os dados relevantes para o gerente de consignado

---

## 🎯 Objetivo

Permitir que o gerente consulte informações de endereço de um cliente e valide se o logradouro informado corresponde ao histórico cadastrado.

---

## ⚙️ Funcionalidades

- 🔎 Consulta de endereço por CEP (READ)
- ✂️ Remoção dos campos desnecessários:
    - IBGE
    - GIA
    - DDD
    - SIAFI
- 🔡 Transformação do campo `logradouro` para lowercase
- 💾 **Cache com Redis** (24 horas TTL)
- ✅ **Validação customizada de CEP**
- 🛡️ **Tratamento global de exceções**
- 📤 Retorno dos dados tratados via API própria

---

## 🧱 Arquitetura

O projeto segue o padrão MVC (Model-View-Controller):

- **Model:** representação dos dados de endereço
- **Controller:** responsável por receber a requisição e retornar a resposta
- **Service:** camada de integração com a API do ViaCEP e regras de negócio
- **Client:** cliente HTTP para consumir ViaCEP
- **Config:** configurações de cache, REST e validação

---

## 🔌 Integração com API externa

Endpoint utilizado:

GET https://viacep.com.br/ws/{cep}/json/

Exemplo:

GET /endereco/01001000

---

## 🔄 Fluxo da aplicação

1. Cliente faz requisição com um CEP
2. Validação do CEP (8 dígitos numéricos)
3. Busca no cache Redis (se existir, retorna imediatamente)
4. API consulta o ViaCEP
5. Dados são tratados:
    - Remoção de campos desnecessários
    - Conversão do logradouro para lowercase
6. Dados são armazenados no cache (TTL 24h)
7. API retorna resposta formatada

---

## 📌 Exemplo de resposta

```json
{
  "cep": "01001-000",
  "logradouro": "praça da sé",
  "complemento": "lado ímpar",
  "bairro": "sé",
  "localidade": "são paulo",
  "uf": "sp"
}
```

---

## 🛠️ Tecnologias Utilizadas

### **Backend**
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.13** - Framework principal
- **Spring Web** - REST APIs
- **Spring Data Redis** - Cache distribuído
- **Spring Validation** - Validação de dados

### **Cache & Testing**
- **Redis** - Cache em produção
- **Embedded Redis** - Mock Redis para testes
- **WireMock** - Mock de API HTTP
- **JUnit 5 + Mockito** - Testes automatizados

### **Logging**
- **Logback** - Logger padrão
- **Logstash Encoder** - Logs em JSON

---

## 📚 Documentação Completa

Para detalhes completos sobre:
- ✅ Tecnologias utilizadas
- ✅ Configurações implementadas
- ✅ Estratégia de testes (24+ testes)
- ✅ Cache com Redis
- ✅ WireMock para testes
- ✅ Como executar

**Veja o arquivo:** [`SOLUCAO.md`](./SOLUCAO.md)

---

## 🚀 Como Começar

### **Pré-requisitos**
- Java 21+
- Maven 3.6+
- Redis (para produção)

### **Executar Aplicação**
```bash
# Instalação de dependências
mvn clean install

# Rodar aplicação (com Redis local ou mock em testes)
mvn spring-boot:run
```

### **Executar Testes**
```bash
# Todos os testes
mvn clean test

# Testes específicos
mvn test -Dtest=EnderecoControllerTest
mvn test -Dtest=EnderecoServiceTest
mvn test -Dtest=ViaCepClientTest
```

---

## 📊 Status do Projeto

- ✅ API RESTful implementada
- ✅ Cache Redis configurado
- ✅ 24+ testes automatizados
- ✅ WireMock para testes de integração
- ✅ Validações customizadas
- ✅ Tratamento global de exceções
- ✅ Logs em JSON
- ✅ Documentação completa

**Projeto pronto para produção!** 🚀
