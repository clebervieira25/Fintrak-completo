package br.com.fintrack.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="usuarios", uniqueConstraints=@UniqueConstraint(name="uk_usuario_email", columnNames="email"))
public class Usuario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String nome;
    @Column(nullable=false, length=180) private String email;
    @Column(nullable=false) private String senha;
    @Column(nullable=false, updatable=false) private Instant criadoEm = Instant.now();
    protected Usuario() {}
    public Usuario(String nome, String email, String senha) { this.nome=nome; this.email=email.toLowerCase(); this.senha=senha; }
    public Long getId(){return id;} public String getNome(){return nome;} public String getEmail(){return email;} public String getSenha(){return senha;}
}
