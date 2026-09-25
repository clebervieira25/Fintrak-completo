package br.com.fintrack.repository;
import br.com.fintrack.model.Categoria; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CategoriaRepository extends JpaRepository<Categoria,Long>{List<Categoria> findAllByUsuarioIdOrderByNome(Long usuarioId); Optional<Categoria> findByIdAndUsuarioId(Long id,Long usuarioId); boolean existsByUsuarioIdAndNomeIgnoreCase(Long usuarioId,String nome);}
