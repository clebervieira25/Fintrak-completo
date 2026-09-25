package br.com.fintrack.model;
public class Categoria {
    private Long id; private String nome; private String cor;
    public Categoria(){} public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getNome(){return nome;} public void setNome(String nome){this.nome=nome;} public String getCor(){return cor;} public void setCor(String cor){this.cor=cor;}
    @Override public String toString(){return nome;}
}
