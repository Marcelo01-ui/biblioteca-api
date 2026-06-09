# Biblioteca API

Microservico RESTful desenvolvido em Java com Spring Boot para gerenciar livros de uma biblioteca. A entidade principal e `Livro`, relacionada a `Autor` por `@ManyToOne`, usando Spring Data JPA, validacoes com Bean Validation, documentacao Swagger/OpenAPI e testes unitarios com JUnit 5, Mockito e JaCoCo.

## Tecnologias

- Java 21
- Spring Boot 4.0.6
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 para desenvolvimento e testes
- PostgreSQL para producao
- Springdoc OpenAPI 3.0.3
- JUnit 5, Mockito e JaCoCo
- Docker, Render ou Heroku para deploy

## Como rodar localmente

Pre-requisitos:

- Java 21 instalado
- Maven instalado
- Git instalado

Passos:

```bash
git clone <url-do-repositorio>
cd biblioteca-api
mvn spring-boot:run
```

A aplicacao ficara disponivel em:

- API: `http://localhost:8080/api/livros`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

Dados do H2:

- JDBC URL: `jdbc:h2:mem:biblioteca`
- Usuario: `sa`
- Senha: vazia

## Rotas da API

| Verbo | Caminho | Descricao |
| --- | --- | --- |
| POST | `/api/livros` | Cria um livro |
| GET | `/api/livros` | Lista todos os livros |
| GET | `/api/livros/{id}` | Busca um livro por ID |
| GET | `/api/livros/isbn/{isbn}` | Busca um livro por ISBN |
| GET | `/api/livros/filtro?autor=Machado&genero=Romance` | Filtra livros por autor e/ou genero |
| PUT | `/api/livros/{id}` | Atualiza todos os dados de um livro |
| PATCH | `/api/livros/{id}/genero` | Atualiza apenas o genero |
| DELETE | `/api/livros/{id}` | Remove um livro |

## Exemplos de uso

Criar livro:

```bash
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Dom Casmurro",
    "isbn": "9788535910663",
    "anoPublicacao": 1899,
    "genero": "Romance",
    "autorNome": "Machado de Assis",
    "autorNacionalidade": "Brasileira"
  }'
```

Listar livros:

```bash
curl http://localhost:8080/api/livros
```

Buscar por ID:

```bash
curl http://localhost:8080/api/livros/1
```

Filtrar por autor:

```bash
curl "http://localhost:8080/api/livros/filtro?autor=Machado"
```

Atualizar livro:

```bash
curl -X PUT http://localhost:8080/api/livros/1 \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Dom Casmurro - Edicao Especial",
    "isbn": "9788535910663",
    "anoPublicacao": 1899,
    "genero": "Realismo",
    "autorNome": "Machado de Assis",
    "autorNacionalidade": "Brasileira"
  }'
```

Atualizar genero:

```bash
curl -X PATCH http://localhost:8080/api/livros/1/genero \
  -H "Content-Type: application/json" \
  -d '{"genero": "Realismo"}'
```

Remover livro:

```bash
curl -X DELETE http://localhost:8080/api/livros/1
```

## Validacoes

O DTO `LivroRequest` valida:

- `titulo`: obrigatorio, maximo de 120 caracteres
- `isbn`: obrigatorio, formato de ISBN 10 ou ISBN 13
- `anoPublicacao`: obrigatorio, entre 1400 e 2100
- `genero`: obrigatorio, maximo de 60 caracteres
- `autorNome`: obrigatorio, maximo de 100 caracteres
- `autorNacionalidade`: obrigatoria, maximo de 60 caracteres

Erros sao retornados no formato padronizado:

```json
{
  "timestamp": "2026-06-01T20:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados invalidos",
  "path": "/api/livros",
  "fields": {
    "titulo": "Titulo e obrigatorio"
  }
}
```

## Testes e cobertura

Executar testes:

```bash
mvn test
```

Gerar relatorio JaCoCo:

```bash
mvn clean test
```

Verificar cobertura minima de 90% nas camadas principais:

```bash
mvn verify
```

Relatorio:

```text
target/site/jacoco/index.html
```

Os testes cobrem cenarios de sucesso e falha nas camadas de servico e controlador, incluindo:

- criacao valida
- ISBN duplicado
- busca por ID inexistente
- busca por ISBN
- validacao de payload invalido
- atualizacao completa
- atualizacao parcial
- exclusao
- filtro por criterios

## Profiles

`dev` usa H2 em memoria:

```text
SPRING_PROFILES_ACTIVE=dev
```

`prod` usa PostgreSQL com variaveis de ambiente:

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:5432/biblioteca
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=senha
DDL_AUTO=update
```

## Docker local com PostgreSQL

Subir o banco:

```bash
docker compose up -d
```

Rodar a aplicacao usando o profile de producao local:

```bash
SPRING_PROFILES_ACTIVE=prod \
DATABASE_URL=jdbc:postgresql://localhost:5432/biblioteca \
DATABASE_USERNAME=biblioteca \
DATABASE_PASSWORD=biblioteca \
mvn spring-boot:run
```

## Deploy em Producao

Link publico da API:

```text
https://substituir-pelo-link-publico-da-api
```

Opcao recomendada: Render + PostgreSQL.

1. Criar um banco PostgreSQL no Render ou no Supabase.
2. Criar um novo Web Service no Render apontando para o repositorio GitHub.
3. Selecionar deploy com Docker, usando o `Dockerfile` deste projeto.
4. Configurar as variaveis de ambiente:

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:5432/biblioteca
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=senha
DDL_AUTO=update
```

5. Publicar o servico e testar:

```bash
curl https://seu-servico.onrender.com/api/livros
```

Opcao alternativa: Heroku.

- O projeto inclui `Procfile`.
- O arquivo `system.properties` define Java 21.
- Usar Heroku Postgres e configurar as variaveis de ambiente equivalentes.

## Controle de versao

Fluxo sugerido:

```bash
git checkout -b feature/cadastro-livros
git add .
git commit -m "feat: adiciona cadastro de livros"
git push origin feature/cadastro-livros
```

Padrao de mensagens:

- `feat: nova funcionalidade`
- `fix: correcao de bug`
- `test: adiciona ou ajusta testes`
- `docs: atualiza documentacao`
- `chore: ajustes de configuracao`

## Divisao de tarefas

| Integrante | Responsabilidade |
| --- | --- |
| Integrante 1 | Modelagem das entidades JPA e repositories |
| Integrante 2 | Implementacao da camada service |
| Integrante 3 | Implementacao dos controllers REST |
| Integrante 4 | DTOs, validacoes e tratamento de excecoes |
| Integrante 5 | Testes unitarios com JUnit 5 e Mockito |
| Integrante 6 | README, Swagger, deploy e apresentacao final |

## Estrutura

```text
src/main/java/com/example/biblioteca
  config
  controller
  dto
  exception
  model
  repository
  service
src/main/resources
src/test/java/com/example/biblioteca
```
## Deploy em Produção

**URL pública:** https://biblioteca-api-3j70.onrender.com
**Swagger:** https://biblioteca-api-3j70.onrender.com/swagger-ui.html

Deploy realizado no Render com banco PostgreSQL.
A aplicação foi conteinerizada com Docker e implantada no Render.
Variáveis de ambiente configuradas no painel do Render (DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD).