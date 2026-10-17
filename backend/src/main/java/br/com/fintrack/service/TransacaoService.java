package br.com.fintrack.service;

import br.com.fintrack.dto.SaldoDTO;
import br.com.fintrack.dto.TransacaoDTO;
import br.com.fintrack.exception.ResourceNotFoundException;
import br.com.fintrack.exception.ValidationException;
import br.com.fintrack.model.Categoria;
import br.com.fintrack.model.Subcategoria;
import br.com.fintrack.model.TipoTransacao;
import br.com.fintrack.model.Transacao;
import br.com.fintrack.model.Usuario;
import br.com.fintrack.repository.CategoriaRepository;
import br.com.fintrack.repository.SubcategoriaRepository;
import br.com.fintrack.repository.TransacaoRepository;
import br.com.fintrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {
    private final TransacaoRepository transacoes;
    private final CategoriaRepository categorias;
    private final SubcategoriaRepository subcategorias;
    private final UsuarioRepository usuarios;

    public TransacaoService(
            TransacaoRepository transacoes,
            CategoriaRepository categorias,
            SubcategoriaRepository subcategorias,
            UsuarioRepository usuarios) {
        this.transacoes = transacoes;
        this.categorias = categorias;
        this.subcategorias = subcategorias;
        this.usuarios = usuarios;
    }

    @Transactional
    public TransacaoDTO criar(Long usuarioId, TransacaoDTO dados) {
        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado."));

        Categoria categoria =
                buscarCategoria(usuarioId, dados.categoriaId());

        Subcategoria subcategoria =
                buscarSubcategoria(
                        usuarioId,
                        dados.categoriaId(),
                        dados.subcategoriaId());

        Transacao transacao = new Transacao(
                dados.data(),
                dados.descricao().trim(),
                dados.valor(),
                dados.tipo(),
                dados.observacao(),
                dados.mensal(),
                usuario,
                categoria,
                subcategoria
        );

        return dto(transacoes.save(transacao));
    }

    @Transactional(readOnly = true)
    public List<TransacaoDTO> listar(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim,
            Long categoriaId) {

        List<Transacao> lista;

        if (categoriaId != null) {
            buscarCategoria(usuarioId, categoriaId);
            lista = transacoes
                    .findByUsuarioIdAndCategoriaIdOrderByDataDesc(
                            usuarioId, categoriaId);
        } else if (inicio != null || fim != null) {
            if (inicio == null || fim == null) {
                throw new ValidationException(
                        "Informe as datas inicial e final.");
            }

            if (inicio.isAfter(fim)) {
                throw new ValidationException(
                        "A data inicial não pode ser posterior à final.");
            }

            lista = transacoes
                    .findByUsuarioIdAndDataBetweenOrderByDataDesc(
                            usuarioId, inicio, fim);
        } else {
            lista = transacoes
                    .findAllByUsuarioIdOrderByDataDesc(usuarioId);
        }

        return lista.stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public TransacaoDTO buscar(Long usuarioId, Long id) {
        return dto(buscarTransacao(usuarioId, id));
    }

    @Transactional
    public TransacaoDTO atualizar(
            Long usuarioId,
            Long id,
            TransacaoDTO dados) {

        Transacao transacao =
                buscarTransacao(usuarioId, id);

        Categoria categoria =
                buscarCategoria(usuarioId, dados.categoriaId());

        Subcategoria subcategoria =
                buscarSubcategoria(
                        usuarioId,
                        dados.categoriaId(),
                        dados.subcategoriaId());

        transacao.atualizar(
                dados.data(),
                dados.descricao().trim(),
                dados.valor(),
                dados.tipo(),
                dados.observacao(),
                dados.mensal(),
                categoria,
                subcategoria
        );

        return dto(transacao);
    }

    @Transactional
    public void remover(Long usuarioId, Long id) {
        transacoes.delete(buscarTransacao(usuarioId, id));
    }

    @Transactional(readOnly = true)
    public SaldoDTO saldo(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim) {

        if (inicio == null) {
            inicio = LocalDate.of(2000, 1, 1);
        }

        if (fim == null) {
            fim = LocalDate.now();
        }

        if (inicio.isAfter(fim)) {
            throw new ValidationException("Período inválido.");
        }

        BigDecimal receitas = transacoes.sumValorPeriodo(
                usuarioId,
                TipoTransacao.RECEITA,
                inicio,
                fim);

        BigDecimal despesas = transacoes.sumValorPeriodo(
                usuarioId,
                TipoTransacao.DESPESA,
                inicio,
                fim);

        return new SaldoDTO(
                receitas,
                despesas,
                receitas.subtract(despesas),
                inicio,
                fim
        );
    }

    private Categoria buscarCategoria(
            Long usuarioId,
            Long categoriaId) {

        return categorias.findByIdAndUsuarioId(
                        categoriaId, usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Categoria não encontrada para este usuário."));
    }

    private Subcategoria buscarSubcategoria(
            Long usuarioId,
            Long categoriaId,
            Long subcategoriaId) {

        if (subcategoriaId == null) {
            return null;
        }

        return subcategorias
                .findByIdAndCategoriaIdAndCategoriaUsuarioId(
                        subcategoriaId,
                        categoriaId,
                        usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subcategoria não encontrada para esta categoria."));
    }

    private Transacao buscarTransacao(
            Long usuarioId,
            Long id) {

        return transacoes.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transação não encontrada."));
    }

    private TransacaoDTO dto(Transacao transacao) {
        Subcategoria subcategoria = transacao.getSubcategoria();

        return new TransacaoDTO(
                transacao.getId(),
                transacao.getData(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getTipo(),
                transacao.getObservacao(),
                transacao.isMensal(),
                transacao.getCategoria().getId(),
                transacao.getCategoria().getNome(),
                subcategoria == null ? null : subcategoria.getId(),
                subcategoria == null ? null : subcategoria.getNome()
        );
    }
}