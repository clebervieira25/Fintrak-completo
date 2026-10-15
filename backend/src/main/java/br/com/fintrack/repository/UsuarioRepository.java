package br.com.fintrack.repository;
import br.com.fintrack.model.Usuario; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
 Optional<Usuario> findByEmailIgnoreCase(String email);
 Optional<Usuario> findByTelefone(String telefone);
 Optional<Usuario> findByEmailAlternativoIgnoreCase(String emailAlternativo);
 boolean existsByEmailIgnoreCase(String email);
 boolean existsByTelefone(String telefone);
 boolean existsByEmailAlternativoIgnoreCase(String emailAlternativo);
}
