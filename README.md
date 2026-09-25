# FinTrack Completo — API + Interface JavaFX

Este pacote integra a etapa final do FinTrack. A pasta `backend` contém a API
Spring Boot segura; a pasta `desktop` contém a interface JavaFX que consome a
API por HTTP e envia o JWT em todas as operações protegidas.

## Executar no IntelliJ

Abra primeiro a pasta `backend` como projeto Maven e execute
`FinTrackApiApplication`. Espere aparecer `Started FinTrackApiApplication`.

Depois abra a pasta `desktop` em outra janela do IntelliJ. No painel Maven,
execute `Plugins > javafx > javafx:run` ou, no terminal:

```powershell
mvn clean javafx:run
```

Na interface:

1. Preencha nome, e-mail e senha e clique em **Criar conta**.
2. Clique em **Nova categoria** e crie `Salário`.
3. Clique em **Nova transação** e cadastre uma receita.
4. Crie a categoria `Alimentação` e cadastre uma despesa.
5. Confira a tabela, o saldo e o relatório.

## Roteiro curto para o vídeo

1. Mostre o terminal com a API Spring Boot em execução.
2. Abra `http://localhost:8080/swagger-ui.html` e mostre as rotas.
3. Abra a interface JavaFX e faça login.
4. Crie uma categoria e uma receita pela interface.
5. Crie uma despesa e mostre o saldo atualizado.
6. Abra o relatório.
7. Volte ao Swagger, autorize com um usuário e liste as transações, mostrando
   que os mesmos dados cadastrados pela interface aparecem na API.
8. Explique que o Hibernate persiste os registros no banco H2 em `backend/data`.

Não é preciso iniciar o antigo projeto JavaFX com SQLite: a pasta `desktop`
deste pacote já está conectada ao `backend`.
