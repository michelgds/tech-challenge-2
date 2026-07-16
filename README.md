# 🍽️ Tech Challenge — Sistema de Gestão de Restaurantes (Fase 2)

API REST para gerenciamento de usuários, tipos de usuário, restaurantes e itens de cardápio, desenvolvida com **Spring Boot 3**, **PostgreSQL**, **Clean Architecture** e **Docker**.

---

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Como executar](#-como-executar)
- [Variáveis de ambiente](#-variáveis-de-ambiente)
- [Endpoints](#-endpoints)
- [Tratamento de erros](#-tratamento-de-erros)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Documentação Swagger](#-documentação-swagger)

---

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem |
| Spring Boot | 3.5.x | Framework principal |
| Spring JDBC (`JdbcClient`) | 6.x | Acesso ao banco de dados |
| Spring Validation | 3.x | Validação de DTOs |
| SpringDoc OpenAPI | 2.8.x | Documentação Swagger |
| PostgreSQL | 16 | Banco de dados relacional |
| Lombok | latest | Redução de boilerplate |
| Docker / Docker Compose | - | Containerização |

---

## 📦 Pré-requisitos

- [Java 17+](https://adoptium.net/)
- [Docker](https://www.docker.com/) >= 24.x
- [Docker Compose](https://docs.docker.com/compose/) >= 2.x
- [Maven](https://maven.apache.org/) (ou usar o `./mvnw` incluso no projeto)

---

## ▶️ Como executar

### Opção 1 — Docker Compose completo (recomendado)

Sobe o banco de dados **e** a aplicação juntos:

```bash
# Primeira execução (faz o build da imagem)
docker compose up --build

# Execuções seguintes
docker compose up
```

### Opção 2 — Apenas o banco via Docker + aplicação local

Ideal para desenvolvimento:

```bash
# Sobe apenas o PostgreSQL
docker compose up db

# Em outro terminal, sobe a aplicação
./mvnw spring-boot:run
```

> O `application.properties` já usa `localhost:5433` como padrão quando `DB_URL` não está definida.

### Comandos úteis

```bash
# Parar os containers
docker compose down

# Parar e remover volumes (limpa o banco)
docker compose down -v

# Ver logs da aplicação
docker compose logs -f app

# Ver logs do banco
docker compose logs -f db
```

---

## 🔧 Variáveis de ambiente

| Variável | Padrão (local) | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/usuarios` | URL de conexão com o banco |
| `DB_USERNAME` | `tech-challenge` | Usuário do banco |
| `DB_PASSWORD` | `tech-challenge` | Senha do banco |

No ambiente Docker, essas variáveis são definidas automaticamente pelo `docker-compose.yml`.

---

## 📡 Endpoints

Base URL: `http://localhost:8080`

### Usuários — `/v1/usuarios`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/usuarios` | Listar usuários com paginação e filtro opcional por nome |
| `GET` | `/v1/usuarios/{id}` | Buscar usuário por ID |
| `POST` | `/v1/usuarios` | Cadastrar novo usuário |
| `PUT` | `/v1/usuarios/{id}` | Atualizar dados do usuário (exceto senha) |
| `PATCH` | `/v1/usuarios/{id}/senha` | Trocar senha |
| `DELETE` | `/v1/usuarios/{id}` | Excluir usuário |
| `POST` | `/v1/usuarios/login` | Validar login e senha |

### Tipos de Usuário — `/v1/tipos-usuario`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/tipos-usuario` | Listar todos os tipos de usuário |
| `GET` | `/v1/tipos-usuario/{id}` | Buscar tipo de usuário por ID |
| `POST` | `/v1/tipos-usuario` | Cadastrar novo tipo de usuário |
| `PUT` | `/v1/tipos-usuario/{id}` | Atualizar tipo de usuário |
| `DELETE` | `/v1/tipos-usuario/{id}` | Excluir tipo de usuário (bloqueado se houver usuários associados) |

### Restaurantes — `/v1/restaurantes`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/restaurantes` | Listar restaurantes com paginação e filtro opcional por nome |
| `GET` | `/v1/restaurantes/{id}` | Buscar restaurante por ID |
| `POST` | `/v1/restaurantes` | Cadastrar novo restaurante |
| `PUT` | `/v1/restaurantes/{id}` | Atualizar restaurante |
| `DELETE` | `/v1/restaurantes/{id}` | Excluir restaurante |

### Itens do Cardápio — `/v1/restaurantes/{restauranteId}/itens-cardapio`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/restaurantes/{restauranteId}/itens-cardapio` | Listar itens do cardápio de um restaurante (paginado) |
| `GET` | `/v1/restaurantes/{restauranteId}/itens-cardapio/{id}` | Buscar item do cardápio por ID |
| `POST` | `/v1/restaurantes/{restauranteId}/itens-cardapio` | Cadastrar novo item do cardápio |
| `PUT` | `/v1/restaurantes/{restauranteId}/itens-cardapio/{id}` | Atualizar item do cardápio |
| `DELETE` | `/v1/restaurantes/{restauranteId}/itens-cardapio/{id}` | Excluir item do cardápio |

### Exemplos

#### Criar tipo de usuário
```bash
curl -X POST http://localhost:8080/v1/tipos-usuario \
  -H "Content-Type: application/json" \
  -d '{"nome": "CLIENTE"}'
```

#### Criar usuário
```bash
curl -X POST http://localhost:8080/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "login": "joao123",
    "senha": "senha123",
    "tipoUsuarioId": 2,
    "endereco": {
      "rua": "Rua das Flores",
      "numero": "123",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01310-100"
    }
  }'
```

#### Listar usuários (paginado)
```bash
curl "http://localhost:8080/v1/usuarios?page=1&size=10"
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "login": "joao123",
    "tipoUsuarioId": 2,
    "tipoUsuarioNome": "CLIENTE",
    "dataUltimaAlteracao": "2026-05-04T10:00:00",
    "endereco": {
      "rua": "Rua das Flores",
      "numero": "123",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01310-100"
    }
  }
]
```

#### Buscar por nome
```bash
curl "http://localhost:8080/v1/usuarios?nome=João"
```

#### Atualizar usuário
```bash
curl -X PUT http://localhost:8080/v1/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Atualizado",
    "email": "joao.novo@email.com",
    "login": "joao123",
    "tipoUsuarioId": 2,
    "endereco": {
      "rua": "Avenida Paulista",
      "numero": "1000",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01311-100"
    }
  }'
```

#### Trocar senha
```bash
curl -X PATCH http://localhost:8080/v1/usuarios/1/senha \
  -H "Content-Type: application/json" \
  -d '{"senhaAtual": "senha123", "novaSenha": "novaSenha456"}'
```

#### Login
```bash
curl -X POST http://localhost:8080/v1/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"login": "joao123", "senha": "senha123"}'
```

#### Criar restaurante
```bash
curl -X POST http://localhost:8080/v1/restaurantes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Cantina da Nonna",
    "endereco": {
      "rua": "Rua Itália",
      "numero": "50",
      "cidade": "São Paulo",
      "estado": "SP",
      "cep": "01310-300"
    },
    "tipoCozinha": "Italiana",
    "horarioFuncionamento": "Seg a Dom, 11h às 23h",
    "donoId": 1
  }'
```

#### Criar item do cardápio
```bash
curl -X POST http://localhost:8080/v1/restaurantes/1/itens-cardapio \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Fettuccine Alfredo",
    "descricao": "Massa fresca ao molho branco cremoso",
    "preco": 49.90,
    "disponivelSomenteLocal": false,
    "fotoPath": "/fotos/fettuccine-alfredo.jpg"
  }'
```

---


Todos os erros seguem o padrão **RFC 7807 (ProblemDetail)**, nativo do Spring 6+:

```json
{
  "type": "about:blank",
  "title": "Recurso não encontrado",
  "status": 404,
  "detail": "Usuário não encontrado com id: 99"
}
```

### Mapeamento de erros

| Situação | Status HTTP | Título |
|---|---|---|
| Credenciais inválidas | `401` | Não autorizado |
| Recurso não encontrado | `404` | Recurso não encontrado |
| Regra de negócio violada (ex: e-mail duplicado, senha incorreta) | `422` | Erro de regra de negócio |
| Campos inválidos (`@Valid`) | `400` | Dados inválidos |
| Parâmetro de query ausente | `400` | Parâmetro ausente |
| Tipo de parâmetro inválido (ex: texto no lugar de número) | `400` | Tipo de parâmetro inválido |
| Conflito de dados | `409` | Conflito de dados |
| Erro inesperado | `500` | Erro interno do servidor |

---

## 🗂️ Estrutura do projeto (Clean Architecture + Use Cases)

```
src/main/java/br/com/fiap/usuarios/
├── domain/                                   # Regras de negócio, sem dependências externas
│   ├── model/                                # Usuario, Endereco, TipoUsuario, Restaurante, ItemCardapio
│   ├── exception/                            # AuthenticationException, BusinessException, ResourceNotFoundException
│   └── repository/                           # Interfaces (ports) dos repositórios
├── application/                              # Casos de uso da aplicação
│   ├── dto/                                  # DTOs de entrada e de resposta, agrupados por módulo
│   ├── mapper/                               # Conversão DTO <-> entidade de domínio (UsuarioMapper, RestauranteMapper, ...)
│   └── usecase/                              # Um caso de uso por classe (Interactor), agrupados por módulo:
│       ├── usuario/                          # Criar, Atualizar, AtualizarSenha, Excluir, Buscar, Listar, Autenticar
│       ├── restaurante/                      # Criar, Atualizar, Excluir, Buscar, Listar
│       ├── tipousuario/                      # Criar, Atualizar, Excluir, Buscar, Listar
│       └── itemcardapio/                     # Criar, Atualizar, Excluir, Buscar, Listar
└── infrastructure/                           # Detalhes técnicos (adapters)
    ├── config/OpenApiConfig.java              # Configuração Swagger/OpenAPI
    ├── security/PasswordEncoderConfig.java     # Bean de PasswordEncoder (BCrypt)
    ├── web/
    │   ├── controller/                        # Controllers REST de cada módulo (finos, delegam aos casos de uso)
    │   └── handler/ControllerExceptionHandler.java  # Tratamento global de erros (RFC 7807)
    └── persistence/jdbc/                      # Implementações dos repositórios com JdbcClient + RowMappers
```

A regra de dependência segue o sentido `infrastructure → application → domain`: o `domain` não depende de nenhuma outra camada, `application` depende apenas de `domain`, e `infrastructure` implementa as portas definidas em `domain` e expõe os casos de uso de `application` via REST.

### 🔒 Segurança de senha

Senhas nunca são armazenadas ou comparadas em texto plano: `CriarUsuarioUseCase` e `AtualizarSenhaUsuarioUseCase` usam `PasswordEncoder` (BCrypt) para gerar o hash, e `AutenticarUsuarioUseCase` valida a senha informada com `passwordEncoder.matches(...)` contra o hash persistido.

---

## 🧪 Testes

- **Testes unitários** (JUnit 5 + Mockito) para os casos de uso de cada módulo (usuário, restaurante, tipo de usuário, item de cardápio), cobrindo cenários de sucesso e de erro (regras de negócio, recursos não encontrados, hashing/verificação de senha).
- **Testes de integração** (Spring Boot Test + MockMvc, banco H2 em modo PostgreSQL) para os 4 controllers, cobrindo o fluxo CRUD completo e os principais cenários de erro (400/404/422).
- Cobertura de código medida com **JaCoCo**, com verificação automática de mínimo de **80% de linhas cobertas** durante o `mvn verify` (`./mvnw clean verify`).

```bash
./mvnw clean verify
# Relatório HTML em target/site/jacoco/index.html
```

---


## 📖 Documentação Swagger

Com a aplicação rodando, acesse:

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

---

## 🐳 Docker

### Containers

| Container | Imagem | Porta |
|---|---|---|
| `tech-challenge-app` | Build local (`Dockerfile`) | `8080` |
| `tech-challenge-db` | `postgres:16-alpine` | `5433` |

### Banco de dados

| Configuração | Valor |
|---|---|
| Nome do banco | `usuarios` |
| Usuário | `tech-challenge` |
| Senha | `tech-challenge` |
| Porta (host) | `5433` |

> O script `data.sql` é executado automaticamente na inicialização e popula o banco com os 2 tipos de usuário padrão (`DONO_RESTAURANTE`, `CLIENTE`), 2 usuários de exemplo (`admin` e `cliente`), 1 restaurante de exemplo e 1 item de cardápio de exemplo.
