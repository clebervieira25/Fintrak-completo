package br.com.fintrack.controller;

import br.com.fintrack.client.ApiClient;
import br.com.fintrack.client.ApiException;
import br.com.fintrack.model.Categoria;
import br.com.fintrack.model.TipoTransacao;
import br.com.fintrack.model.Transacao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TransacaoFormController {
    @FXML private DatePicker campoData;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoValor;
    @FXML private ComboBox<TipoTransacao> campoTipo;
    @FXML private TextArea campoObservacao;
    @FXML private CheckBox campoMensal;
    @FXML private ComboBox<Categoria> campoCategoria;

    private Transacao transacao;
    private Runnable aoSalvar;

    @FXML
    private void initialize() {
        campoTipo.setItems(FXCollections.observableArrayList(TipoTransacao.values()));
        campoData.setValue(LocalDate.now());
        campoTipo.setValue(TipoTransacao.DESPESA);
        campoCategoria.setItems(FXCollections.observableArrayList(ApiClient.categorias()));
    }

    public void preparar(Transacao transacao, Runnable aoSalvar) {
        this.transacao = transacao;
        this.aoSalvar = aoSalvar;
        if (transacao != null) {
            campoData.setValue(transacao.getData());
            campoDescricao.setText(transacao.getDescricao());
            campoValor.setText(String.valueOf(transacao.getValor()).replace('.', ','));
            campoTipo.setValue(transacao.getTipo());
            campoObservacao.setText(transacao.getObservacao());
            campoMensal.setSelected(transacao.isMensal());
            campoCategoria.getItems().stream().filter(c -> c.getId().equals(transacao.getCategoriaId())).findFirst().ifPresent(campoCategoria::setValue);
        }
    }

    @FXML
    private void salvar() {
        try {
            double valor = Double.parseDouble(campoValor.getText().trim().replace(',', '.'));
            if (transacao == null) {
                transacao = new Transacao(null, campoData.getValue(), campoDescricao.getText(),
                        valor, campoTipo.getValue(), campoObservacao.getText(), campoMensal.isSelected());
            } else {
                transacao.setData(campoData.getValue());
                transacao.setDescricao(campoDescricao.getText());
                transacao.setValor(valor);
                transacao.setTipo(campoTipo.getValue());
                transacao.setObservacao(campoObservacao.getText());
                transacao.setMensal(campoMensal.isSelected());
            }
            if (campoCategoria.getValue() == null) throw new IllegalArgumentException("Selecione uma categoria.");
            transacao.setCategoriaId(campoCategoria.getValue().getId());
            ApiClient.salvar(transacao);
            aoSalvar.run();
            fechar();
        } catch (NumberFormatException e) {
            mostrarErro("Digite um valor válido, como 150,50.");
        } catch (IllegalArgumentException | NullPointerException | ApiException e) {
            mostrarErro(e.getMessage() == null ? "Preencha todos os campos obrigatórios." : e.getMessage());
        }
    }

    @FXML private void cancelar() { fechar(); }
    private void fechar() { ((Stage) campoDescricao.getScene().getWindow()).close(); }
    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem, ButtonType.OK);
        alerta.setHeaderText("Dados inválidos");
        alerta.showAndWait();
    }
}
