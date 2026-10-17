package br.com.fintrack.controller;

import br.com.fintrack.client.ApiClient;
import br.com.fintrack.model.TipoTransacao;
import br.com.fintrack.model.Transacao;
import br.com.fintrack.utils.Formatador;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioController {
    private static final String SEM_CATEGORIA = "Sem categoria";

    @FXML private Label labelReceitas;
    @FXML private Label labelDespesas;
    @FXML private Label labelSaldo;
    @FXML private Label labelQuantidade;
    @FXML private Label labelDetalhes;

    @FXML private DatePicker filtroInicio;
    @FXML private DatePicker filtroFim;
    @FXML private ComboBox<String> filtroCategoria;
    @FXML private ComboBox<TipoTransacao> filtroTipo;

    @FXML private TableView<CategoriaResumo> tabelaCategorias;
    @FXML private TableColumn<CategoriaResumo, String> colunaCategoria;
    @FXML private TableColumn<CategoriaResumo, Integer> colunaQuantidadeCategoria;
    @FXML private TableColumn<CategoriaResumo, String> colunaReceitasCategoria;
    @FXML private TableColumn<CategoriaResumo, String> colunaDespesasCategoria;
    @FXML private TableColumn<CategoriaResumo, String> colunaSaldoCategoria;

    @FXML private TableView<Transacao> tabelaDetalhes;
    @FXML private TableColumn<Transacao, String> colunaData;
    @FXML private TableColumn<Transacao, String> colunaDescricao;
    @FXML private TableColumn<Transacao, String> colunaObservacao;
    @FXML private TableColumn<Transacao, String> colunaTipo;
    @FXML private TableColumn<Transacao, String> colunaValor;

    private List<Transacao> todasTransacoes = List.of();
    private List<Transacao> transacoesFiltradas = List.of();

    @FXML
    private void initialize() {
        configurarColunas();
        todasTransacoes = ApiClient.transacoes();
        carregarFiltros();

        tabelaCategorias.getSelectionModel().selectedItemProperty().addListener(
                (observavel, anterior, atual) -> mostrarDetalhes(atual));

        aplicarFiltros();
    }

    private void configurarColunas() {
        colunaCategoria.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nome()));
        colunaQuantidadeCategoria.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().quantidade()).asObject());
        colunaReceitasCategoria.setCellValueFactory(d -> new SimpleStringProperty(Formatador.moeda(d.getValue().receitas())));
        colunaDespesasCategoria.setCellValueFactory(d -> new SimpleStringProperty(Formatador.moeda(d.getValue().despesas())));
        colunaSaldoCategoria.setCellValueFactory(d -> new SimpleStringProperty(Formatador.moeda(d.getValue().saldo())));

        colunaData.setCellValueFactory(d -> new SimpleStringProperty(Formatador.data(d.getValue().getData())));
        colunaDescricao.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescricao()));
        colunaObservacao.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getObservacao()));
        colunaTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().toString()));
        colunaValor.setCellValueFactory(d -> new SimpleStringProperty(Formatador.moeda(d.getValue().valorComSinal())));
    }

    private void carregarFiltros() {
        List<String> categorias = todasTransacoes.stream()
                .map(this::nomeCategoria)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        filtroCategoria.setItems(FXCollections.observableArrayList(categorias));
        filtroTipo.setItems(FXCollections.observableArrayList(TipoTransacao.values()));
    }

    @FXML
    private void aplicarFiltros() {
        LocalDate inicio = filtroInicio.getValue();
        LocalDate fim = filtroFim.getValue();
        String categoria = filtroCategoria.getValue();
        TipoTransacao tipo = filtroTipo.getValue();

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            new Alert(Alert.AlertType.WARNING, "A data inicial não pode ser posterior à data final.", ButtonType.OK).showAndWait();
            return;
        }

        transacoesFiltradas = todasTransacoes.stream()
                .filter(t -> inicio == null || !t.getData().isBefore(inicio))
                .filter(t -> fim == null || !t.getData().isAfter(fim))
                .filter(t -> categoria == null || nomeCategoria(t).equals(categoria))
                .filter(t -> tipo == null || t.getTipo() == tipo)
                .sorted(Comparator.comparing(Transacao::getData).reversed())
                .collect(Collectors.toList());

        atualizarResumoGeral();
        atualizarCategorias();
    }

    private void atualizarResumoGeral() {
        double receitas = somar(transacoesFiltradas, TipoTransacao.RECEITA);
        double despesas = somar(transacoesFiltradas, TipoTransacao.DESPESA);
        labelReceitas.setText(Formatador.moeda(receitas));
        labelDespesas.setText(Formatador.moeda(despesas));
        labelSaldo.setText(Formatador.moeda(receitas - despesas));
        labelQuantidade.setText(String.valueOf(transacoesFiltradas.size()));
    }

    private void atualizarCategorias() {
        Map<String, List<Transacao>> porCategoria = transacoesFiltradas.stream()
                .collect(Collectors.groupingBy(this::nomeCategoria, TreeMap::new, Collectors.toList()));

        List<CategoriaResumo> resumos = porCategoria.entrySet().stream()
                .map(entry -> {
                    double receitas = somar(entry.getValue(), TipoTransacao.RECEITA);
                    double despesas = somar(entry.getValue(), TipoTransacao.DESPESA);
                    return new CategoriaResumo(entry.getKey(), entry.getValue().size(), receitas, despesas);
                })
                .collect(Collectors.toList());

        tabelaCategorias.setItems(FXCollections.observableArrayList(resumos));
        tabelaDetalhes.getItems().clear();
        labelDetalhes.setText("Lançamentos da categoria");
        if (!resumos.isEmpty()) tabelaCategorias.getSelectionModel().selectFirst();
    }

    private void mostrarDetalhes(CategoriaResumo resumo) {
        if (resumo == null) {
            tabelaDetalhes.getItems().clear();
            labelDetalhes.setText("Lançamentos da categoria");
            return;
        }

        List<Transacao> detalhes = transacoesFiltradas.stream()
                .filter(t -> nomeCategoria(t).equals(resumo.nome()))
                .collect(Collectors.toList());
        tabelaDetalhes.setItems(FXCollections.observableArrayList(detalhes));
        labelDetalhes.setText("Lançamentos: " + resumo.nome());
    }

    private double somar(List<Transacao> transacoes, TipoTransacao tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    private String nomeCategoria(Transacao transacao) {
        String nome = transacao.getCategoriaNome();
        return nome == null || nome.isBlank() ? SEM_CATEGORIA : nome;
    }

    @FXML
    private void limparFiltros() {
        filtroInicio.setValue(null);
        filtroFim.setValue(null);
        filtroCategoria.setValue(null);
        filtroTipo.setValue(null);
        aplicarFiltros();
    }

    @FXML
    private void fechar() {
        ((Stage) labelSaldo.getScene().getWindow()).close();
    }

    public record CategoriaResumo(String nome, int quantidade, double receitas, double despesas) {
        public double saldo() {
            return receitas - despesas;
        }
    }
}
