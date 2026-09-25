package br.com.fintrack.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="transacoes", indexes={@Index(name="idx_transacao_usuario_data",columnList="usuario_id,data"),@Index(name="idx_transacao_categoria",columnList="categoria_id")})
public class Transacao {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private LocalDate data;
    @Column(nullable=false,length=160) private String descricao;
    @Column(nullable=false,precision=15,scale=2) private BigDecimal valor;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private TipoTransacao tipo;
    @Column(length=500) private String observacao;
    @Column(nullable=false) private boolean mensal;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="categoria_id",nullable=false) private Categoria categoria;
    @Column(nullable=false,updatable=false) private Instant criadoEm=Instant.now();
    protected Transacao() {}
    public Transacao(LocalDate data,String descricao,BigDecimal valor,TipoTransacao tipo,String observacao,boolean mensal,Usuario usuario,Categoria categoria){atualizar(data,descricao,valor,tipo,observacao,mensal,categoria);this.usuario=usuario;}
    public void atualizar(LocalDate data,String descricao,BigDecimal valor,TipoTransacao tipo,String observacao,boolean mensal,Categoria categoria){this.data=data;this.descricao=descricao;this.valor=valor;this.tipo=tipo;this.observacao=observacao;this.mensal=mensal;this.categoria=categoria;}
    public Long getId(){return id;} public LocalDate getData(){return data;} public String getDescricao(){return descricao;} public BigDecimal getValor(){return valor;} public TipoTransacao getTipo(){return tipo;} public String getObservacao(){return observacao;} public boolean isMensal(){return mensal;} public Usuario getUsuario(){return usuario;} public Categoria getCategoria(){return categoria;}
}
