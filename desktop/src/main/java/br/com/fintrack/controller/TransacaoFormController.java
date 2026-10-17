package br.com.fintrack.controller;

import br.com.fintrack.client.ApiClient;
import br.com.fintrack.client.ApiException;
import br.com.fintrack.model.Categoria;
import br.com.fintrack.model.Subcategoria;
import br.com.fintrack.model.TipoTransacao;
import br.com.fintrack.model.Transacao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
    @FXML private ComboBox<Subcategoria> campoSubcategoria;

    private Transacao transacao;
    private Runnable aoSalvar;

    @FXML
    private void initialize() {
        campoTipo.setItems(
                FXCollections.observableArrayList(
                        TipoTransacao.values()));

        campoData.setValue(LocalDate.now());
        campoTipo.setValue(TipoTransacao.DESPESA);

        campoCategoria.setItems(
                FXCollections.observableArrayList(
                        ApiClient.categorias()));

        campoCategoria.valueProperty().addListener(
                (observavel, anterior, atual) ->
                        carregarSubcategorias(atual));
    }

    public void preparar(
            Transacao transacao,
            Runnable aoSalvar) {

        this.transacao = transacao;
        this.aoSalvar = aoSalvar;

        if (transacao == null) {
            return;
        }

        campoData.setValue(transacao.getData());
        campoDescricao.setText(transacao.getDescricao());
        campoValor.setText(
                String.valueOf(transacao.getValor())
                        .replace('.', ','));
        campoTipo.setValue(transacao.getTipo());
        campoObservacao.setText(
                transacao.getObservacao());
        campoMensal.setSelected(
                transacao.isMensal());

        campoCategoria.getItems()
                .stream()
                .filter(categoria ->
                        categoria.getId().equals(
                                transacao.getCategoriaId()))
                .findFirst()
                .ifPresent(campoCategoria::setValue);

        if (transacao.getSubcategoriaId() != null) {
            campoSubcategoria.getItems()
                    .stream()
                    .filter(subcategoria ->
                            subcategoria.getId().equals(
                                    transacao.getSubcategoriaId()))
                    .findFirst()
                    .ifPresent(campoSubcategoria::setValue);
        }
    }

    private void carregarSubcategorias(
            Categoria categoria) {

        campoSubcategoria.getItems().clear();
        campoSubcategoria.setValue(null);
        campoSubcategoria.setDisable(categoria == null);

        if (categoria == null) {
            return;
        }

        try {
            campoSubcategoria.setItems(
                    FXCollections.observableArrayList(
                            ApiClient.subcategorias(
                                    categoria.getId())));
        } catch (ApiException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void criarSubcategoria() {
        Categoria categoria =
                campoCategoria.getValue();

        if (categoria == null) {
            mostrarErro(
                    "Selecione primeiro uma categoria.");
            return;
        }

        TextInputDialog dialogo =
                new TextInputDialog();

        dialogo.setTitle("Nova subcategoria");
        dialogo.setHeaderText(
                "Criar subcategoria para " +
                        categoria.getNome());
        dialogo.setContentText("Nome:");

        dialogo.showAndWait().ifPresent(nome -> {
            String nomeTratado = nome.trim();

            if (nomeTratado.isBlank()) {
                mostrarErro(
                        "Informe o nome da subcategoria.");
                return;
            }

            try {
                Subcategoria criada =
                        ApiClient.criarSubcategoria(
                                categoria.getId(),
                                nomeTratado);

                carregarSubcategorias(categoria);

                campoSubcategoria.getItems()
                        .stream()
                        .filter(item ->
                                item.getId().equals(
                                        criada.getId()))
                        .findFirst()
                        .ifPresent(
                                campoSubcategoria::setValue);
            } catch (ApiException e) {
                mostrarErro(e.getMessage());
            }
        });
    }

    @FXML
    private void salvar() {
        try {
            double valor = Double.parseDouble(
                    campoValor.getText()
                            .trim()
                            .replace(',', '.'));

            if (transacao == null) {
                transacao = new Transacao(
                        null,
                        campoData.getValue(),
                        campoDescricao.getText(),
                        valor,
                        campoTipo.getValue(),
                        campoObservacao.getText(),
                        campoMensal.isSelected());
            } else {
                transacao.setData(
                        campoData.getValue());
                transacao.setDescricao(
                        campoDescricao.getText());
                transacao.setValor(valor);
                transacao.setTipo(
                        campoTipo.getValue());
                transacao.setObservacao(
                        campoObservacao.getText());
                transacao.setMensal(
                        campoMensal.isSelected());
            }

            Categoria categoria =
                    campoCategoria.getValue();

            if (categoria == null) {
                throw new IllegalArgumentException(
                        "Selecione uma categoria.");
            }

            transacao.setCategoriaId(
                    categoria.getId());

            Subcategoria subcategoria =
                    campoSubcategoria.getValue();

            transacao.setSubcategoriaId(
                    subcategoria == null
                            ? null
                            : subcategoria.getId());

            ApiClient.salvar(transacao);

            if (aoSalvar != null) {
                aoSalvar.run();
            }

            fechar();
        } catch (NumberFormatException e) {
            mostrarErro(
                    "Digite um valor válido, como 150,50.");
        } catch (IllegalArgumentException |
                 NullPointerException |
                 ApiException e) {
            mostrarErro(
                    e.getMessage() == null
                            ? "Preencha todos os campos obrigatórios."
                            : e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        ((Stage) campoDescricao
                .getScene()
                .getWindow())
                .close();
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                mensagem,
                ButtonType.OK);

        alerta.setHeaderText("Dados inválidos");
        alerta.showAndWait();
    }
}