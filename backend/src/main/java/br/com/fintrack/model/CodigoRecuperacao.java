package br.com.fintrack.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="codigos_recuperacao",indexes=@Index(name="idx_codigo_usuario",columnList="usuario_id"))
public class CodigoRecuperacao {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
    @Column(nullable=false) private String codigoHash;
    @Column(nullable=false) private Instant expiraEm;
    @Column(nullable=false) private boolean usado;
    @Column(nullable=false) private int tentativas;
    protected CodigoRecuperacao(){}
    public CodigoRecuperacao(Usuario usuario,String codigoHash,Instant expiraEm){this.usuario=usuario;this.codigoHash=codigoHash;this.expiraEm=expiraEm;}
    public Usuario getUsuario(){return usuario;} public String getCodigoHash(){return codigoHash;} public Instant getExpiraEm(){return expiraEm;} public boolean isUsado(){return usado;} public int getTentativas(){return tentativas;}
    public boolean expirado(){return Instant.now().isAfter(expiraEm);}
    public void registrarTentativa(){tentativas++;}
    public void marcarUsado(){usado=true;}
}
