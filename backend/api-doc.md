# Documentação da APIs

Este documento fornece informações detalhadas sobre os endpoints da API de Autenticação, Gerenciamento de Usuários e Gerenciamento de Exercícios, incluindo suas funcionalidades, estruturas de requisição e resposta, e possíveis códigos de status.

## Índice
1. [Visão Geral da API de Autenticação](#visão-geral-da-api-de-autenticação)
2. [Visão Geral da API de Gerenciamento de Usuários](#visão-geral-da-api-de-gerenciamento-de-usuários)
3. [Visão Geral da API de Gerenciamento de Exercícios](#visão-geral-da-api-de-gerenciamento-de-exercícios)
4. [Endpoints](#endpoints)
    - [Endpoints de Autenticação](#endpoints-de-autenticação)
        - [1. Login de Usuário](#1-login-de-usuário)
        - [2. Solicitar Redefinição de Senha](#2-solicitar-redefinição-de-senha)
        - [3. Redefinir Senha](#3-redefinir-senha)
    - [Endpoints de Gerenciamento de Usuários](#endpoints-de-gerenciamento-de-usuários)
        - [4. Obter Usuário por ID](#4-obter-usuário-por-id)
        - [5. Criar Usuário](#5-criar-usuário)
        - [6. Excluir Usuário](#6-excluir-usuário)
        - [7. Buscar Usuários](#7-buscar-usuários)
    - [Endpoints de Gerenciamento de Exercícios](#endpoints-de-gerenciamento-de-exercícios)
        - [8. Criar Exercício](#8-criar-exercício)
        - [9. Obter Exercício por ID](#9-obter-exercício-por-id)
        - [10. Atualizar Exercício](#10-atualizar-exercício)
        - [11. Excluir Exercício](#11-excluir-exercício)
        - [12. Buscar Exercícios](#12-buscar-exercícios)
5. [Respostas de Erro](#respostas-de-erro)
6. [Autenticação](#autenticação)
7. [Notas](#notas)

## Visão Geral da API de Autenticação
A API de Autenticação lida com os processos de autenticação de usuários, incluindo login, solicitação de redefinição de senha e redefinição de senha. Ela garante acesso seguro a recursos protegidos emitindo e validando tokens.

## Visão Geral da API de Gerenciamento de Usuários
A API de Gerenciamento de Usuários permite gerenciar dados de usuários dentro do sistema. Fornece funcionalidades para recuperar informações de usuários, criar novos usuários, excluir usuários existentes e buscar usuários com base em critérios específicos.

## Visão Geral da API de Gerenciamento de Exercícios
A API de Gerenciamento de Exercícios facilita a criação, recuperação, atualização, exclusão e busca de exercícios no sistema. Permite que usuários autorizados gerenciem o conteúdo de exercícios de maneira eficiente, garantindo que os exercícios estejam organizados, acessíveis e atualizados.

## Endpoints

### Endpoints de Autenticação

#### 1. Login de Usuário
**Endpoint:** `/auth/login`  
**Método:** `POST`  
**Descrição:** Autentica um usuário utilizando seu email e senha. Após a autenticação bem-sucedida, retorna um token para acesso autorizado.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
- **Body:**
    ```json
    {
        "email": "string",
        "password": "string"
    }
    ```

**Respostas:**
- **200 OK:**
    - **Body:**
    ```json
    {
        "token": "string"
    }
    ```
- **400 Requisição Inválida:**
    - **Body:**
    ```json
    {
        "message": "string"
    }
    ```
- **401 Não Autorizado:**
    - **Body:**
    ```json
    {
        "message": "Credenciais inválidas"
    }
    ```

#### 2. Solicitar Redefinição de Senha
**Endpoint:** `/auth/reset-password/request`  
**Método:** `POST`  
**Descrição:** Inicia o processo de redefinição de senha enviando um link ou código de redefinição para o email fornecido.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
- **Body:**
    ```json
    {
        "email": "string"
    }
    ```

**Respostas:**
- **204 Sem Conteúdo**

- **400 Requisição Inválida:**
    - **Body:**
    ```json
    {
        "message": "Email ausente"
    }
    ```

#### 3. Redefinir Senha
**Endpoint:** `/auth/reset-password`  
**Método:** `POST`  
**Descrição:** Redefine a senha do usuário utilizando um token válido.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "password": "string"
    }
    ```

**Respostas:**
- **200 OK:**
    - **Body:**
    ```json
    {
        "message": "Senha redefinida com sucesso"
    }
    ```

- **400 Requisição Inválida:**  
- **401 Não Autorizado**  
- **404 Não Encontrado**  

### Endpoints de Gerenciamento de Usuários

#### 4. Obter Usuário por ID
**Endpoint:** `/user/{id}`  
**Método:** `GET`  
**Descrição:** Recupera um usuário pelo seu ID.

**Requisição:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Respostas:**
- **200 OK:** Retorna os detalhes do usuário.
- **401 Não Autorizado**  
- **404 Não Encontrado**  

#### 5. Criar Usuário
**Endpoint:** `/user`  
**Método:** `POST`  
**Descrição:** Cria um novo usuário.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "name": "string",
        "email": "string"
    }
    ```

**Respostas:**
- **201 Criado**
- **400 Requisição Inválida**
- **401 Não Autorizado**

#### 6. Excluir Usuário
**Endpoint:** `/user/{id}`  
**Método:** `DELETE`  
**Descrição:** Exclui um usuário pelo ID.

**Requisição:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Respostas:**
- **204 Sem Conteúdo**
- **401 Não Autorizado**  
- **404 Não Encontrado**

#### 7. Buscar Usuários
**Endpoint:** `/user/search`  
**Método:** `POST`  
**Descrição:** Busca usuários com base em critérios fornecidos. Suporta paginação via parâmetros de query.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Query Parameters:**
    - `limit` (inteiro, opcional): O número máximo de usuários a serem retornados.
    - `offset` (inteiro, opcional): O número de usuários a serem ignorados antes de coletar o conjunto de resultados.
- **Body:**
    ```json
    {
        "searchTerm": "string"
    }
    ```

**Respostas:**
- **200 OK:**
    - **Body:**
    ```json
    {
        "users": [
            {
                "id": "integer",
                "name": "string",
                "email": "string"
                "roles": ["string"]
            }
            // ... mais usuários
        ],
        "total": "integer"
    }
    ```
- **400 Requisição Inválida**
- **401 Não Autorizado**

### Endpoints de Gerenciamento de Exercícios

#### 8. Criar Exercício
**Endpoint:** `/exercise`  
**Método:** `POST`  
**Descrição:** Cria um novo exercício.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "title": "string",
        "description": "string",
        "tags": [{"value": "string"}],
        "possibleAnswers": ["string"],
        "correctAnswerIndex": "integer"
    }
    ```

**Respostas:**
- **201 Criado**
- **400 Requisição Inválida**
- **401 Não Autorizado**

#### 9. Obter Exercício por ID
**Endpoint:** `/exercise/{id}`  
**Método:** `GET`  
**Descrição:** Recupera um exercício pelo ID.

**Requisição:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Respostas:**
- **200 OK:** Retorna os detalhes do exercício.
    - **Body:**
    ```json
    {
        "id": "string",
        "title": "string",
        "description": "string",
        "tags": [
            {
                "value": "string"
            }
        ],
        "possibleAnswers": ["string"],
        "correctAnswerIndex": "integer"
    }
    ```
- **401 Não Autorizado**  
- **404 Não Encontrado**

#### 10. Atualizar Exercício
**Endpoint:** `/exercise/{id}`  
**Método:** `PUT`  
**Descrição:** Atualiza um exercício pelo ID.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "title": "string",
        "description": "string",
        "tags": [{"value": "string"}],
        "possibleAnswers": ["string"],
        "correctAnswerIndex": "integer"
    }
    ```

**Respostas:**
- **204 Sem Conteúdo**
- **400 Requisição Inválida**  
- **401 Não Autorizado**  
- **404 Não Encontrado**

#### 11. Excluir Exercício
**Endpoint:** `/exercise/{id}`  
**Método:** `DELETE`  
**Descrição:** Exclui um exercício pelo ID.

**Requisição:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Respostas:**
- **204 Sem Conteúdo**
- **401 Não Autorizado**  
- **404 Não Encontrado**

#### 12. Buscar Exercícios
**Endpoint:** `/exercise/search`  
**Método:** `POST`  
**Descrição:** Busca exercícios com base em critérios fornecidos. Suporta paginação via parâmetros de query.

**Requisição:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Query Parameters:**
    - `limit` (inteiro, opcional): O número máximo de exercícios a serem retornados.
    - `offset` (inteiro, opcional): O número de exercícios a serem ignorados antes de coletar o conjunto de resultados.
- **Body:**
    ```json
    {
        "searchTerm": "string",
        "tags": [
            {
                "tag": {
                    "value": "string"
                }
            }
        ]
    }
    ```

**Respostas:**
- **200 OK:**
    - **Body:**
    ```json
    {
        "exercises": [
            {
                "id": "string",
                "title": "string",
                "description": "string",
                "tags": [
                    {
                        "value": "string"
                    }
                ],
                "possibleAnswers": ["string"],
                "correctAnswerIndex": "integer"
            }
            // ... mais exercícios
        ],
        "total": "integer"
    }
    ```
- **400 Requisição Inválida**
- **401 Não Autorizado**

## Respostas de Erro
A API utiliza códigos de status HTTP padrão:
- **400 Requisição Inválida:** Parâmetros ausentes ou inválidos.
- **401 Não Autorizado:** Token inválido.
- **404 Não Encontrado:** Recurso não encontrado.
- **204 Sem Conteúdo:** requisição bem-sucedida, mas sem conteúdo para enviar na resposta.

## Autenticação
Para acessar endpoints protegidos, inclua o seguinte header:

```makefile
Authorization: Bearer <token>
