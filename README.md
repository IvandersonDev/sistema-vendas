# Sistema de Vendas (Spring Boot)

API REST completa para cadastro de clientes e produtos, emissão de pedidos, validações, segurança básica, migrações de banco (Flyway), testes, Swagger e CI com GitHub Actions.

## Tecnologias
- Java 21, Spring Boot 3.3
- Spring Web, Validation, Data JPA, Security, Actuator
- H2 Database (dev/test), Flyway
- springdoc-openapi (Swagger UI)
- Maven, JUnit, MockMvc
- Dockerfile e GitHub Actions

## Executar localmente
1. Requisitos: Java 21 + Maven.
2. Rodar: `mvn spring-boot:run`
3. Swagger UI: http://localhost:8080/swagger-ui
4. H2 Console: http://localhost:8080/h2-console (JDBC: `jdbc:h2:mem:vendasdb`)
5. Healthcheck: http://localhost:8080/actuator/health

Credenciais padrão (HTTP Basic):
- user / user123 (ROLE_USER) [GET]
- admin / admin123 (ROLE_ADMIN, ROLE_USER) [GET/POST/PUT/DELETE]

## Endpoints principais
- Clientes: `/api/clientes` (GET/POST/PUT/DELETE)
- Produtos: `/api/produtos` (GET/POST/PUT/DELETE)
- Pedidos: `/api/pedidos` (GET, POST)
  - Pagar: `/api/pedidos/{id}/pagar` (POST)
  - Cancelar: `/api/pedidos/{id}/cancelar` (POST)

Exemplo (criar pedido):
```json
POST /api/pedidos
{
  "clienteId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 2, "quantidade": 1 }
  ]
}
```

## Banco de dados
- Migrações em `src/main/resources/db/migration`
- `V1__init.sql` cria as tabelas; `V2__seed.sql` insere dados de exemplo.

## Testes
- Executar: `mvn verify`
- Testes de serviço e integração com MockMvc.

## Docker
- Build: `docker build -t sistema-vendas .`
- Run: `docker run -p 8080:8080 sistema-vendas`

## Estrutura
- `src/main/java/com/example/vendas` código da aplicação
- `api` controllers e DTOs; `domain` entidades, repositórios e serviços; `core` configs

## Licença
Uso livre para estudos e portfolios.
