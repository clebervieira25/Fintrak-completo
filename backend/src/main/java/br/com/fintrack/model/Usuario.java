package br.com.fintrack.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="usuarios", uniqueConstraints={
    @UniqueConstraint(name="uk_usuario_email", columnNames="email"),
    @UniqueConstraint(name="uk_usuario_telefone", columnNames="telefone"),
    @UniqueConstraint(name="uk_usuario_email_alternativo", columnNames="email_alternativo")
})
public class Usuario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String nome;
    @Column(nullable=false, length=180) private String email;
    @Column(nullable=false) private String senha;
    @Column(length=20) private String telefone;
    @Column(name="email_alternativo", length=180) private String emailAlternativo;
    @Column(nullable=false, updatable=false) private Instant criadoEm = Instant.now();
    protected Usuario() {}
    public Usuario(String nome, String email, String senha) { this(nome,email,senha,null,null); }
    public Usuario(String nome,String email,String senha,String telefone,String emailAlternativo){this.nome=nome;this.email=email.toLowerCase();this.senha=senha;this.telefone=telefone;this.emailAlternativo=emailAlternativo==null?null:emailAlternativo.toLowerCase();}
    public Long getId(){return id;} public String getNome(){return nome;} public String getEmail(){return email;} public String getSenha(){return senha;} public String getTelefone(){return telefone;} public String getEmailAlternativo(){return emailAlternativo;}
    public void alterarSenha(String senha){this.senha=senha;}
}
