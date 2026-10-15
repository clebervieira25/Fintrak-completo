package br.com.fintrack.controller;

import br.com.fintrack.client.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RecuperacaoController {
    @FXML private TextField campoEmail;
    @FXML private TextField campoCodigo;
    @FXML private PasswordField campoNovaSenha;
    @FXML private Label mensagem;

    @FXML private void enviarCodigo() {
        try {
            validarEmail();
            var resposta = ApiClient.solicitarRecuperacao(campoEmail.getText().trim(), "EMAIL");
            mensagem.setStyle("-fx-text-fill:#18794e");
            mensagem.setText(resposta.mensagem());
        } catch (Exception e) {
            erro(e.getMessage());
        }
    }

    @FXML private void redefinir() {
        try {
            validarEmail();
            if (!campoCodigo.getText().matches("\\d{6}")) throw new ApiException("Informe o código de 6 dígitos.");
            if (campoNovaSenha.getText().length() < 8) throw new ApiException("A nova senha deve ter pelo menos 8 caracteres.");
            var resposta = ApiClient.confirmarRecuperacao(
                    campoEmail.getText().trim(), "EMAIL", campoCodigo.getText(), campoNovaSenha.getText());
            LoginController.abrirPrincipal(resposta);
        } catch (Exception e) {
            erro(e.getMessage());
        }
    }

    private void validarEmail() {
        String email = campoEmail.getText().trim();
        if (email.isBlank() || !email.contains("@")) throw new ApiException("Informe o e-mail cadastrado.");
    }

    private void erro(String texto) {
        mensagem.setStyle("-fx-text-fill:#b42318");
        mensagem.setText(texto);
    }
}
