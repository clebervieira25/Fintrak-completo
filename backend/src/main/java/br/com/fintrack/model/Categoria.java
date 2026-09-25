package br.com.fintrack.model;

import jakarta.persistence.*;

@Entity @Table(name="categorias", uniqueConstraints=@UniqueConstraint(name="uk_categoria_usuario_nome", columnNames={"usuario_id","nome"}))
public class Categoria {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=80) private String nome;
    @Column(length=7) private String cor;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="usuario_id", nullable=false) private Usuario usuario;
    protected Categoria() {}
    public Categoria(String nome, String cor, Usuario usuario){this.nome=nome;this.cor=cor;this.usuario=usuario;}
    public Long getId(){return id;} public String getNome(){return nome;} public String getCor(){return cor;} public Usuario getUsuario(){return usuario;}
    public void atualizar(String nome,String cor){this.nome=nome;this.cor=cor;}
}
