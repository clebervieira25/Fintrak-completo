package br.com.fintrack.controller;

import br.com.fintrack.app.FinApp;
import br.com.fintrack.client.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Label mensagem;

    @FXML private void login() { autenticar(false); }
    @FXML private void registrar() { autenticar(true); }

    private void autenticar(boolean novo) {
        try {
            if (novo && campoNome.getText().isBlank()) throw new ApiException("Informe o nome.");
            if (campoEmail.getText().isBlank()) throw new ApiException("Informe o e-mail.");
            if (campoSenha.getText().length() < 8) throw new ApiException("A senha deve ter pelo menos 8 caracteres.");
            var r = novo
                    ? ApiClient.registrar(campoNome.getText(), campoEmail.getText(), campoSenha.getText())
                    : ApiClient.login(campoEmail.getText(), campoSenha.getText());
            abrirPrincipal(r);
        } catch (Exception e) {
            mensagem.setText(e.getMessage());
        }
    }

    @FXML private void abrirRecuperacao() {
        try {
            FXMLLoader l = new FXMLLoader(FinApp.class.getResource("/fxml/recuperacao.fxml"));
            Scene s = new Scene(l.load(), 520, 500);
            s.getStylesheets().add(FinApp.class.getResource("/css/estilo.css").toExternalForm());
            Stage janela = new Stage();
            janela.setTitle("Recuperar senha");
            janela.initOwner(campoEmail.getScene().getWindow());
            janela.setScene(s);
            janela.setMinWidth(500);
            janela.setMinHeight(480);
            janela.show();
        } catch (Exception e) {
            mensagem.setText("Não foi possível abrir a recuperação de senha.");
        }
    }

    static void abrirPrincipal(ApiClient.TokenResponse r) throws Exception {
        Sessao.iniciar(r.token(), r.usuarioId(), r.nome());
        FXMLLoader l = new FXMLLoader(FinApp.class.getResource("/fxml/principal.fxml"));
        Scene s = new Scene(l.load(), 920, 600);
        s.getStylesheets().add(FinApp.class.getResource("/css/estilo.css").toExternalForm());
        var janelasAntigas = new java.util.ArrayList<javafx.stage.Window>(javafx.stage.Window.getWindows().filtered(javafx.stage.Window::isShowing));
        Stage stage = new Stage();
        stage.setTitle("FinTrack - Controle Financeiro");
        stage.setScene(s);
        stage.setMinWidth(820);
        stage.setMinHeight(520);
        stage.centerOnScreen();
        stage.show();
        for (javafx.stage.Window janela : janelasAntigas) janela.hide();
    }
}
