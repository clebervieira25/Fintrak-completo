# FinTrack Desktop

Cliente JavaFX do FinTrack. Esta versão não acessa diretamente SQLite/JDBC:
todas as ações são enviadas para a API em `http://localhost:8080` com JWT.

Inicie primeiro `../backend` e depois execute:

```powershell
mvn clean javafx:run
```
