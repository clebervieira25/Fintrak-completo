# FinTrack API — Etapa Final

API RESTful segura e multiusuário para controle de finanças pessoais. Construída
com Spring Boot, Spring Web, Spring Data JPA/Hibernate, Spring Security, JWT,
Bean Validation, H2/PostgreSQL e OpenAPI/Swagger.

## Requisitos

- IntelliJ IDEA
- JDK 17
- Maven 3.9 ou o Maven integrado ao IntelliJ

## Abrir e executar no IntelliJ

1. Extraia `FinTrack-API.zip`.
2. No IntelliJ, escolha **File > Open** e abra a pasta `FinTrack-API`.
3. Clique em **Trust Project**, se solicitado.
4. Aguarde o carregamento das dependências do `pom.xml`.
5. Em **File > Project Structure > Project**, selecione o JDK 17.
6. Abra `src/main/java/br/com/fintrack/FinTrackApiApplication.java`.
7. Clique no triângulo verde ao lado de `main` e em **Run**.

Alternativamente, no PowerShell aberto na pasta do projeto:

```powershell
mvn clean spring-boot:run
```

A API inicia em `http://localhost:8080`. O H2 cria os arquivos locais dentro da
pasta `data`, sem exigir instalação de servidor de banco.

## Swagger

Abra no navegador:

```text
http://localhost:8080/swagger-ui.html
```

Fluxo recomendado para testar:

1. Use `POST /api/auth/register` para criar um usuário.
2. Copie o valor de `token` retornado.
3. Clique em **Authorize** no topo do Swagger.
4. Cole somente o token. O Swagger acrescenta `Bearer` automaticamente.
5. Crie uma categoria.
6. Crie receitas e despesas. Despesas podem deixar o saldo negativo.
7. Consulte o relatório de saldo.

Exemplo de registro:

```json
{
  "nome": "Michelle",
  "email": "michelle@example.com",
  "senha": "SenhaSegura123",
  "telefone": "+5585999999999",
  "emailAlternativo": "michelle.recuperacao@example.com"
}
```

O telefone deve utilizar o formato internacional: `+55`, DDD e número, sem
espaços. O e-mail alternativo precisa ser diferente do e-mail principal.

## Recuperação de acesso por SMS e e-mail

O código possui seis dígitos, expira em dez minutos, aceita no máximo cinco
tentativas e só pode ser utilizado uma vez. Após a confirmação, a API redefine
a senha e retorna um JWT, permitindo que a interface entre na conta e mostre o
e-mail principal ao usuário.

### Configurar SMS com Twilio

Crie uma conta e um número remetente no Twilio. Antes de iniciar a API, informe
as credenciais no mesmo PowerShell:

```powershell
$env:TWILIO_ACCOUNT_SID="AC..."
$env:TWILIO_AUTH_TOKEN="seu-token"
$env:TWILIO_FROM_NUMBER="+1..."
```

Em contas de avaliação, o telefone destinatário precisa estar autorizado no
painel do Twilio. Nunca coloque o token diretamente no código ou no GitHub.

### Configurar e-mail real

O exemplo abaixo usa SMTP do Gmail. Ative a verificação em duas etapas e crie
uma senha de aplicativo; não use a senha normal da conta:

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="seuemail@gmail.com"
$env:MAIL_PASSWORD="sua-senha-de-aplicativo"
$env:MAIL_FROM="seuemail@gmail.com"
```

Execute `mvn spring-boot:run` no mesmo terminal em que definiu as variáveis.
Elas precisam ser configuradas novamente em outro computador.

### Fluxo da recuperação

1. `POST /api/auth/recovery/request` recebe `destino` e `canal` (`SMS` ou `EMAIL`).
2. A API envia o código ao telefone ou e-mail alternativo previamente cadastrado.
3. `POST /api/auth/recovery/confirm` recebe destino, canal, código e nova senha.
4. A API redefine a senha e devolve o token e o e-mail principal.

Exemplo de categoria:

```json
{
  "nome": "Alimentação",
  "cor": "#2E8B57"
}
```

Exemplo de transação — use o ID da categoria criada:

```json
{
  "data": "2026-08-30",
  "descricao": "Salário",
  "valor": 3500.00,
  "tipo": "RECEITA",
  "observacao": "Pagamento mensal",
  "mensal": true,
  "categoriaId": 1
}
```

## Rotas

| Método | Rota | Resultado |
|---|---|---|
| POST | `/api/auth/register` | Registra usuário e retorna JWT |
| POST | `/api/auth/login` | Autentica e retorna JWT |
| POST | `/api/auth/recovery/request` | Envia código de recuperação |
| POST | `/api/auth/recovery/confirm` | Redefine senha e retorna JWT |
| GET/POST | `/api/v1/usuarios/{usuarioId}/categorias` | Lista/cria categorias próprias |
| PUT/DELETE | `/api/v1/usuarios/{usuarioId}/categorias/{id}` | Altera/remove categoria própria |
| GET/POST | `/api/v1/usuarios/{usuarioId}/transacoes` | Lista/cria transações próprias |
| GET/PUT/DELETE | `/api/v1/usuarios/{usuarioId}/transacoes/{id}` | CRUD por ID |
| GET | `/api/v1/usuarios/{usuarioId}/transacoes/relatorios/saldo` | Saldo por período |

A listagem aceita `inicio`, `fim` e `categoriaId` como parâmetros. O relatório
aceita `inicio` e `fim` no formato `AAAA-MM-DD`.

## Segurança e confidencialidade

- Senhas são armazenadas com BCrypt, nunca em texto puro.
- Códigos de recuperação também são armazenados como hash BCrypt.
- A API não utiliza sessão: cada requisição protegida exige JWT.
- O JWT expira após duas horas.
- `@PreAuthorize` compara o ID da URL com o ID do usuário autenticado.
- Repositórios e serviços buscam recursos por `recursoId + usuarioId`.
- Uma categoria de outro usuário não pode ser associada a uma transação.
- O servidor responde 401 sem autenticação e 403 ao tentar usar outro
  `usuarioId`.
- A tentativa de acessar um ID pertencente a outro usuário não revela o recurso.

Em produção, defina uma chave Base64 secreta própria na variável `JWT_SECRET`.
Nunca publique essa chave no Git.

## PostgreSQL opcional

Inicie o banco incluído:

```powershell
docker compose up -d
```

Depois execute com o perfil PostgreSQL:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

As variáveis `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` podem
substituir os valores locais.

## Testes

```powershell
mvn test
```

Os testes unitários de `TransacaoService` validam o cálculo de saldo e confirmam
que despesas podem ser cadastradas mesmo quando deixam o saldo negativo.

## Organização

```text
src/main/java/br/com/fintrack/
├── config/       Segurança e OpenAPI
├── controller/   Endpoints REST
├── dto/          Objetos de entrada e saída
├── exception/    Exceções e tratamento global
├── model/        Entidades JPA
├── repository/   Spring Data JPA e JPQL
├── security/     JWT e usuário autenticado
└── service/      Regras de negócio e mapeamento DTO/entidade
```

## Principais códigos HTTP

- `200`: consulta ou alteração concluída;
- `201`: usuário, categoria ou transação criada;
- `204`: remoção concluída;
- `400`: dados inválidos, código expirado ou serviço de envio não configurado;
- `401`: JWT ausente/inválido ou login incorreto;
- `403`: tentativa de acessar outro usuário;
- `404`: recurso próprio não encontrado;
- `409`: e-mail, telefone ou categoria duplicada, ou categoria ainda em uso.
