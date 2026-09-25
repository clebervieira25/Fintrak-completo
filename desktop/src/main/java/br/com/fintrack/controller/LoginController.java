package br.com.fintrack.controller;
import br.com.fintrack.app.FinApp; import br.com.fintrack.client.*; import javafx.fxml.*; import javafx.scene.*; import javafx.scene.control.*; import javafx.stage.Stage;
public class LoginController {
 @FXML private TextField campoNome; @FXML private TextField campoEmail; @FXML private PasswordField campoSenha; @FXML private Label mensagem;
 @FXML private void login(){autenticar(false);} @FXML private void registrar(){autenticar(true);}
 private void autenticar(boolean novo){try{if(novo&&campoNome.getText().isBlank())throw new ApiException("Informe o nome.");var r=novo?ApiClient.registrar(campoNome.getText(),campoEmail.getText(),campoSenha.getText()):ApiClient.login(campoEmail.getText(),campoSenha.getText());Sessao.iniciar(r.token(),r.usuarioId(),r.nome());FXMLLoader l=new FXMLLoader(FinApp.class.getResource("/fxml/principal.fxml"));Scene s=new Scene(l.load(),920,600);s.getStylesheets().add(FinApp.class.getResource("/css/estilo.css").toExternalForm());Stage stage=(Stage)campoEmail.getScene().getWindow();stage.setScene(s);stage.setMinWidth(820);stage.setMinHeight(520);stage.centerOnScreen();}catch(Exception e){mensagem.setText(e.getMessage());}}
}
