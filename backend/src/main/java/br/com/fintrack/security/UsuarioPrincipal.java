package br.com.fintrack.security;
import br.com.fintrack.model.Usuario; import org.springframework.security.core.*; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.userdetails.UserDetails; import java.util.*;
public record UsuarioPrincipal(Long id,String nome,String email,String senha) implements UserDetails{
 public static UsuarioPrincipal de(Usuario u){return new UsuarioPrincipal(u.getId(),u.getNome(),u.getEmail(),u.getSenha());}
 public Collection<? extends GrantedAuthority> getAuthorities(){return List.of(new SimpleGrantedAuthority("ROLE_USER"));}
 public String getPassword(){return senha;} public String getUsername(){return email;}
 public boolean isAccountNonExpired(){return true;} public boolean isAccountNonLocked(){return true;} public boolean isCredentialsNonExpired(){return true;} public boolean isEnabled(){return true;}
}
