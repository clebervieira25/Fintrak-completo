package br.com.fintrack.repository;

import br.com.fintrack.model.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    List<Subcategoria> findAllByCategoriaIdAndCategoriaUsuarioIdOrderByNome(
        Long categoriaId, Long usuarioId
    );

    Optional<Subcategoria> findByIdAndCategoriaIdAndCategoriaUsuarioId(
        Long id, Long categoriaId, Long usuarioId
    );

    boolean existsByCategoriaIdAndNomeIgnoreCase(Long categoriaId, String nome);
}
