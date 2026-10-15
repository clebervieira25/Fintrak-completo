package br.com.fintrack.repository;

import br.com.fintrack.model.CodigoRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoRecuperacaoRepository extends JpaRepository<CodigoRecuperacao,Long>{
    Optional<CodigoRecuperacao> findFirstByUsuarioIdAndUsadoFalseOrderByIdDesc(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}
