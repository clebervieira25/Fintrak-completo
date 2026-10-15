# FinTrack Desktop

Cliente JavaFX do FinTrack. Esta versão não acessa diretamente SQLite/JDBC:
todas as ações são enviadas para a API em `http://localhost:8080` com JWT.

Inicie primeiro `../backend` e depois execute:

```powershell
mvn clean javafx:run
```

Para criar uma nova conta, informe também telefone no formato internacional e
um e-mail alternativo. O link **Esqueci meu e-mail ou minha senha** abre a tela
de recuperação por SMS ou e-mail. O backend precisa estar configurado com as
credenciais de envio descritas em `../backend/README.md`.
