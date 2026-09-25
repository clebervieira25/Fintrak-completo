package br.com.fintrack.controller;

import br.com.fintrack.client.ApiClient;
import br.com.fintrack.model.Transacao;
import br.com.fintrack.utils.Formatador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;

public class RelatorioController {
    @FXML private Label labelReceitas;
    @FXML private Label labelDespesas;
    @FXML private Label labelSaldo;
    @FXML private Label labelQuantidade;

    @FXML
    private void initialize() {
        var saldo=ApiClient.saldo();
        labelReceitas.setText(Formatador.moeda(saldo.totalReceitas()));
        labelDespesas.setText(Formatador.moeda(saldo.totalDespesas()));
        labelSaldo.setText(Formatador.moeda(saldo.saldo()));
        labelQuantidade.setText(String.valueOf(ApiClient.transacoes().size()));
    }

    @FXML private void fechar() { ((Stage) labelSaldo.getScene().getWindow()).close(); }
}
