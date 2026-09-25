package br.com.fintrack.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.io.Decoders; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import javax.crypto.SecretKey; import java.time.*; import java.util.Date;
@Service public class JwtService{
 private final SecretKey chave; private final Duration duracao;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration:PT2H}") Duration duracao){this.chave=Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));this.duracao=duracao;}
 public String gerar(UsuarioPrincipal p){Instant agora=Instant.now();return Jwts.builder().subject(p.getUsername()).claim("uid",p.id()).issuedAt(Date.from(agora)).expiration(Date.from(agora.plus(duracao))).signWith(chave).compact();}
 public String extrairEmail(String token){return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload().getSubject();}
}
