package br.com.fintrack.service;
import br.com.fintrack.dto.AuthDTOs.*; import br.com.fintrack.exception.ConflitoException; import br.com.fintrack.model.Usuario; import br.com.fintrack.repository.UsuarioRepository; import br.com.fintrack.security.*; import org.springframework.security.authentication.*; import org.springframework.security.core.Authentication; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service public class AuthService{
 private final UsuarioRepository usuarios; private final PasswordEncoder encoder; private final AuthenticationManager manager; private final JwtService jwt;
 public AuthService(UsuarioRepository u,PasswordEncoder e,AuthenticationManager m,JwtService j){usuarios=u;encoder=e;manager=m;jwt=j;}
 @Transactional public TokenResponse registrar(RegistroRequest d){if(usuarios.existsByEmailIgnoreCase(d.email()))throw new ConflitoException("E-mail já cadastrado.");Usuario u=usuarios.save(new Usuario(d.nome().trim(),d.email().trim(),encoder.encode(d.senha())));return token(UsuarioPrincipal.de(u));}
 public TokenResponse login(LoginRequest d){Authentication a=manager.authenticate(new UsernamePasswordAuthenticationToken(d.email(),d.senha()));return token((UsuarioPrincipal)a.getPrincipal());}
 private TokenResponse token(UsuarioPrincipal p){return new TokenResponse(jwt.gerar(p),"Bearer",p.id(),p.nome(),p.email());}
}
