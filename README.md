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
- 📤 Retorno dos dados tratados via API própria

---

## 🧱 Arquitetura

O projeto segue o padrão MVC (Model-View-Controller):

- Model: representação dos dados de endereço
- Controller: responsável por receber a requisição e retornar a resposta
- Service: camada de integração com a API do ViaCEP e regras de negócio

---

## 🔌 Integração com API externa

Endpoint utilizado:

GET https://viacep.com.br/ws/{cep}/json/

Exemplo:

GET /endereco/01001000

---

## 🔄 Fluxo da aplicação

1. Cliente faz requisição com um CEP
2. API consulta o ViaCEP
3. Dados são tratados:
    - Remoção de campos desnecessários
    - Conversão do logradouro para lowercase
4. API retorna resposta formatada

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
