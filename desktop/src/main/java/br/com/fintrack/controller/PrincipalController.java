package br.com.fintrack.controller;

import br.com.fintrack.app.FinApp;
import br.com.fintrack.client.ApiClient;
import br.com.fintrack.client.ApiException;
import br.com.fintrack.model.Transacao;
import br.com.fintrack.utils.Formatador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PrincipalController {
    @FXML private TableView<Transacao> tabelaTransacoes;
    @FXML private TableColumn<Transacao, String> colunaData;
    @FXML private TableColumn<Transacao, String> colunaDescricao;
    @FXML private TableColumn<Transacao, String> colunaValor;
    @FXML private TableColumn<Transacao, String> colunaTipo;
    @FXML private TableColumn<Transacao, String> colunaMensal;
    @FXML private Label labelSaldo;
    @FXML private Button botaoNova;
    @FXML private Button botaoRelatorio;

    @FXML
    private void initialize() {
        colunaData.setCellValueFactory(d -> new SimpleStringProperty(Formatador.data(d.getValue().getData())));
        colunaDescricao.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescricao()));
        colunaValor.setCellValueFactory(d -> new SimpleStringProperty(Formatador.moeda(d.getValue().valorComSinal())));
        colunaTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().toString()));
        colunaMensal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isMensal() ? "Sim" : "Não"));
        botaoNova.setOnAction(evento -> novaTransacao());
        botaoRelatorio.setOnAction(evento -> abrirRelatorio());
        atualizarTela();
    }

    @FXML private void novaTransacao() { abrirFormulario(null); }

    @FXML
    private void editarTransacao() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alerta(Alert.AlertType.WARNING, "Seleção necessária", "Selecione uma transação para editar.");
            return;
        }
        abrirFormulario(selecionada);
    }

    @FXML
    private void removerTransacao() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alerta(Alert.AlertType.WARNING, "Seleção necessária", "Selecione uma transação para remover.");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja remover “" + selecionada.getDescricao() + "”?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText("Confirmar remoção");
        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try { ApiClient.remover(selecionada.getId()); atualizarTela(); }
            catch (ApiException e) { alerta(Alert.AlertType.ERROR,"Erro",e.getMessage()); }
        }
    }

    @FXML
    private void abrirRelatorio() {
        try {
            FXMLLoader loader = new FXMLLoader(FinApp.class.getResource("/fxml/relatorio.fxml"));
            Parent raiz = loader.load();
            Stage janela = criarJanela("Relatório financeiro", raiz);
            janela.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o relatório: " + e.getMessage());
        }
    }

    private void abrirFormulario(Transacao transacao) {
        try {
            FXMLLoader loader = new FXMLLoader(FinApp.class.getResource("/fxml/transacao-form.fxml"));
            Parent raiz = loader.load();
            TransacaoFormController controller = loader.getController();
            controller.preparar(transacao, this::atualizarTela);
            Stage janela = criarJanela(transacao == null ? "Nova transação" : "Editar transação", raiz);
            janela.showAndWait();
        } catch (IOException e) {
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o formulário.");
        }
    }

    private Stage criarJanela(String titulo, Parent raiz) {
        Stage janela = new Stage();
        Scene scene = new Scene(raiz);
        scene.getStylesheets().add(FinApp.class.getResource("/css/estilo.css").toExternalForm());
        janela.setTitle(titulo);
        janela.initModality(Modality.APPLICATION_MODAL);
        janela.initOwner(tabelaTransacoes.getScene().getWindow());
        janela.setScene(scene);
        return janela;
    }

    private void atualizarTela() {
        try {
            tabelaTransacoes.setItems(FXCollections.observableArrayList(ApiClient.transacoes()));
            labelSaldo.setText(Formatador.moeda(ApiClient.saldo().saldo()));
        } catch (ApiException e) { alerta(Alert.AlertType.ERROR,"API indisponível",e.getMessage()); }
    }

    @FXML private void novaCategoria() {
        TextInputDialog nome = new TextInputDialog(); nome.setHeaderText("Nova categoria"); nome.setContentText("Nome:");
        nome.showAndWait().ifPresent(n -> { if(n.isBlank()) return; TextInputDialog cor=new TextInputDialog("#2E8B57");cor.setHeaderText("Cor da categoria");cor.setContentText("Cor (#RRGGBB):");cor.showAndWait().ifPresent(c->{try{ApiClient.criarCategoria(n,c);alerta(Alert.AlertType.INFORMATION,"Categoria","Categoria criada com sucesso.");}catch(ApiException e){alerta(Alert.AlertType.ERROR,"Erro",e.getMessage());}}); });
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
