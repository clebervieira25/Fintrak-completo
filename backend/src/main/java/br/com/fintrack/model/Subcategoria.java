package br.com.fintrack.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "subcategorias",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_subcategoria_categoria_nome",
        columnNames = {"categoria_id", "nome"}
    )
)
public class Subcategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    protected Subcategoria() {}

    public Subcategoria(String nome, Categoria categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Categoria getCategoria() { return categoria; }

    public void atualizar(String nome) {
        this.nome = nome;
    }
}
