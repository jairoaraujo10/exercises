# Documentação de Desenvolvimento

Este documento fornece instruções e informações para desenvolvedores que desejam contribuir ou executar este projeto localmente.

## Índice

- [Visão Geral](#visão-geral)
- [Estrutura de Diretórios](#estrutura-de-diretórios)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Construção e Execução](#construção-e-execução)
  - [Usando Docker Compose](#usando-docker-compose)
  - [Desenvolvimento Local (Sem Docker)](#desenvolvimento-local-sem-docker)
- [Problemas Comuns e Soluções](#problemas-comuns-e-soluções)

## Visão Geral

Este projeto é uma aplicação web full-stack composta por:

- **Backend**: Desenvolvido em Kotlin, com dependências gerenciadas via Maven.
- **Frontend**: Desenvolvido em React com TypeScript, utilizando o Vite como ferramenta de build.
- **Banco de Dados**: MySQL 8.0.
- **Conteinerização**: Utilização do Docker e Docker Compose para orquestração dos serviços.
- **Servidor Web**: Nginx configurado para servir o frontend e encaminhar as requisições da API para o backend.

## Estrutura de Diretórios

```bash
projeto/
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── package-lock.json
│   ├── nginx.conf
│   └── Dockerfile
└── docker-compose.yml
```

- **backend/**: Contém o código-fonte do servidor e seu Dockerfile.
- **frontend/**: Contém o código-fonte do cliente, o arquivo de configuração do Nginx (`nginx.conf`) e seu Dockerfile.
- **docker-compose.yml**: Arquivo de orquestração dos contêineres Docker.

## Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

- **Docker**: Para construir e executar os contêineres.
- **Docker Compose**: Para orquestrar múltiplos contêineres.

## Configuração do Ambiente

### Variáveis de Ambiente

As variáveis de ambiente estão definidas no arquivo [`docker-compose.yml`](docker-compose.yml) e podem ser ajustadas conforme necessário.

**Banco de Dados (MySQL):**

- `MYSQL_DATABASE`: Nome do banco de dados (padrão: `exercises`).
- `MYSQL_ROOT_PASSWORD`: Senha do usuário root do MySQL (padrão: `root`).

**Backend:**

- `MYSQL_URL`: URL de conexão com o MySQL.
- `MYSQL_USERNAME`: Nome de usuário do banco de dados (padrão: `root`).
- `MYSQL_PASSWORD`: Senha do banco de dados (padrão: `root`).
- `JWT_SECRET`: Chave secreta para geração de tokens JWT.
- `SMTP_*`: Configurações para o servidor SMTP.
- `ADMIN_*`: Credenciais do administrador padrão.

## Construção e Execução

### Usando Docker Compose

1. **Construir e Iniciar os Contêineres:**

   Navegue até o diretório raiz do projeto e execute:

   ```bash
   docker-compose up --build
   ```

   Isso irá:

   - Construir as imagens Docker para o backend e frontend.
   - Iniciar os serviços definidos no [`docker-compose.yml`](docker-compose.yml).

2. **Acessar a Aplicação:**

   - **Frontend:** `http://localhost:80`
   - **API via Proxy Nginx:** `http://localhost:80/api`
   - **Backend API direta:** `http://localhost:8080`
   - **MySQL:** Porta `3306` (caso precise acessar diretamente)

   > **Nota:** O Nginx está configurado para servir o frontend e encaminhar as requisições que começam com `/api` para o backend. Portanto, o frontend pode fazer chamadas para a API usando o mesmo domínio e porta (`/api`).

3. **Parar os Contêineres**

   Para parar e remover os contêineres, execute:

   ```bash
   docker-compose down
   ```

### Desenvolvimento Local (Sem Docker)

#### Backend

1. **Pré-requisitos:**

   - Java JDK instalado (versão 21 ou compatível).
   - Maven instalado.
   - Banco de dados MySQL rodando localmente.

2. **Passos:**

   ```bash
   cd backend
   mvn clean install
   java -jar target/nomedoarquivo.jar
   ```

   - Certifique-se de configurar as variáveis de ambiente ou o arquivo de configuração para apontar para o seu banco de dados local.

#### Frontend

1. **Pré-requisitos:**

   - Node.js e npm instalados.

2. **Passos:**

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   - Acesse `http://localhost:5173` no navegador.

   > **Nota:** Durante o desenvolvimento local, você pode configurar o proxy do Vite para encaminhar as requisições da API para o backend. Adicione o seguinte ao seu `vite.config.js`:

   ```javascript
   export default defineConfig({
     // ... outras configurações ...
     server: {
       proxy: {
         '/api': 'http://localhost:8080',
       },
     },
   });
   ```

## Problemas Comuns e Soluções

- **Porta em Uso:**

  - Certifique-se de que as portas `80`, `8080` e `3306` não estejam sendo usadas por outros serviços.

- **Erros de Conexão com o Banco de Dados:**

  - Verifique as credenciais e a URL de conexão no serviço `backend`.
  - Certifique-se de que o serviço `mysql` está em execução e acessível.

- **Falha na Construção do Contêiner:**

  - Verifique se todas as dependências estão corretamente especificadas.
  - Confira se há arquivos não rastreados que precisam ser incluídos.

---

**Observação:** As informações de credenciais e configurações usadas no projeto são exemplos e devem ser atualizadas conforme as necessidades do ambiente.
