package br.com.fintrack.repository;

import br.com.fintrack.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;

public interface TransacaoRepository extends JpaRepository<Transacao,Long>{
    List<Transacao> findByUsuarioIdAndDataBetweenOrderByDataDesc(Long usuarioId,LocalDate inicio,LocalDate fim);
    List<Transacao> findByUsuarioIdAndCategoriaIdOrderByDataDesc(Long usuarioId,Long categoriaId);
    List<Transacao> findAllByUsuarioIdOrderByDataDesc(Long usuarioId);
    Optional<Transacao> findByIdAndUsuarioId(Long id,Long usuarioId);
    boolean existsByCategoriaIdAndUsuarioId(Long categoriaId,Long usuarioId);
    boolean existsBySubcategoriaIdAndUsuarioId(Long subcategoriaId,Long usuarioId);
    @Query("select coalesce(sum(t.valor),0) from Transacao t where t.usuario.id=:usuarioId and t.tipo=:tipo")
    BigDecimal sumValorByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId,@Param("tipo") TipoTransacao tipo);
    @Query("select coalesce(sum(t.valor),0) from Transacao t where t.usuario.id=:usuarioId and t.tipo=:tipo and t.data between :inicio and :fim")
    BigDecimal sumValorPeriodo(@Param("usuarioId") Long usuarioId,@Param("tipo") TipoTransacao tipo,@Param("inicio") LocalDate inicio,@Param("fim") LocalDate fim);
}
