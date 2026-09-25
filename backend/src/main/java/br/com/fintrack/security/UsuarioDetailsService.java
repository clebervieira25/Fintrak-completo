package br.com.fintrack.security;
import br.com.fintrack.repository.UsuarioRepository; import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service;
@Service public class UsuarioDetailsService implements UserDetailsService{
 private final UsuarioRepository repository; public UsuarioDetailsService(UsuarioRepository r){repository=r;}
 public UserDetails loadUserByUsername(String email){return repository.findByEmailIgnoreCase(email).map(UsuarioPrincipal::de).orElseThrow(()->new UsernameNotFoundException("Usuário não encontrado"));}
}
