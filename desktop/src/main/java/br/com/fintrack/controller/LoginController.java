package br.com.fintrack.controller;

import br.com.fintrack.app.FinApp;
import br.com.fintrack.client.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class LoginController {
    private static final String CHAVE_EMAIL = "ultimoEmail";

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private TextField campoSenhaVisivel;
    @FXML private CheckBox lembrarEmail;
    @FXML private ToggleButton botaoMostrarSenha;
    @FXML private Label mensagem;

    private final Preferences preferencias = Preferences.userNodeForPackage(LoginController.class);

    @FXML
    private void initialize() {
        campoSenha.textProperty().bindBidirectional(campoSenhaVisivel.textProperty());

        String emailSalvo = preferencias.get(CHAVE_EMAIL, "");
        if (!emailSalvo.isBlank()) {
            campoEmail.setText(emailSalvo);
            lembrarEmail.setSelected(true);
            campoSenha.requestFocus();
        }
    }

    @FXML private void login() { autenticar(false); }
    @FXML private void registrar() { autenticar(true); }

    @FXML
    private void alternarSenha() {
        boolean mostrar = botaoMostrarSenha.isSelected();
        campoSenhaVisivel.setVisible(mostrar);
        campoSenhaVisivel.setManaged(mostrar);
        campoSenha.setVisible(!mostrar);
        campoSenha.setManaged(!mostrar);
        botaoMostrarSenha.setText(mostrar ? "Ocultar senha" : "Mostrar senha");

        TextField campoAtivo = mostrar ? campoSenhaVisivel : campoSenha;
        campoAtivo.requestFocus();
        campoAtivo.positionCaret(campoAtivo.getText().length());
    }

    private void autenticar(boolean novo) {
        try {
            if (novo && campoNome.getText().isBlank()) throw new ApiException("Informe o nome.");
            if (campoEmail.getText().isBlank()) throw new ApiException("Informe oe -mail.");
            if (campoSenha.getText().length() < 8) throw new ApiException("A senha deve ter pelo menos 8 caracteres.");

            String email = campoEmail.getText().trim();
            var r = novo
                    ? ApiClient.registrar(campoNome.getText(), email, campoSenha.getText())
                    : ApiClient.login(email, campoSenha.getText());

            salvarPreferenciaEmail(email);
            abrirPrincipal(r);
        } catch (Exception e) {
            mensagem.setText(e.getMessage());
        }
    }

    private void salvarPreferenciaEmail(String email) {
        if (lembrarEmail.isSelected()) {
            preferencias.put(CHAVE_EMAIL, email);
        } else {
            preferencias.remove(CHAVE_EMAIL);
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
