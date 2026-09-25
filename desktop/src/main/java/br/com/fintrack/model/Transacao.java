package br.com.fintrack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.Objects;

public class Transacao {
    private Integer id;
    private LocalDate data;
    private String descricao;
    private double valor;
    private TipoTransacao tipo;
    private String observacao;
    private boolean mensal;
    private Long categoriaId;
    private String categoriaNome;

    public Transacao() {}

    public Transacao(Integer id, LocalDate data, String descricao, double valor,
                     TipoTransacao tipo, String observacao, boolean mensal) {
        setId(id);
        setData(data);
        setDescricao(descricao);
        setValor(valor);
        setTipo(tipo);
        setObservacao(observacao);
        setMensal(mensal);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = Objects.requireNonNull(data, "A data é obrigatória."); }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) throw new IllegalArgumentException("A descrição é obrigatória.");
        this.descricao = descricao.trim();
    }
    public double getValor() { return valor; }
    public void setValor(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("O valor deve ser maior que zero.");
        this.valor = valor;
    }
    public TipoTransacao getTipo() { return tipo; }
    public void setTipo(TipoTransacao tipo) { this.tipo = Objects.requireNonNull(tipo, "O tipo é obrigatório."); }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao == null ? "" : observacao.trim(); }
    public boolean isMensal() { return mensal; }
    public void setMensal(boolean mensal) { this.mensal = mensal; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

    @JsonIgnore
    public double valorComSinal() {
        return tipo == TipoTransacao.RECEITA ? valor : -valor;
    }
}
