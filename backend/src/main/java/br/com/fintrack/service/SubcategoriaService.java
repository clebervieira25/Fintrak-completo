package br.com.fintrack.service;

import br.com.fintrack.dto.SubcategoriaDTO;
import br.com.fintrack.exception.ConflitoException;
import br.com.fintrack.exception.ResourceNotFoundException;
import br.com.fintrack.model.Categoria;
import br.com.fintrack.model.Subcategoria;
import br.com.fintrack.repository.CategoriaRepository;
import br.com.fintrack.repository.SubcategoriaRepository;
import br.com.fintrack.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubcategoriaService {
    private final SubcategoriaRepository subcategorias;
    private final CategoriaRepository categorias;
    private final TransacaoRepository transacoes;

    public SubcategoriaService(
            SubcategoriaRepository subcategorias,
            CategoriaRepository categorias,
            TransacaoRepository transacoes) {
        this.subcategorias = subcategorias;
        this.categorias = categorias;
        this.transacoes = transacoes;
    }

    @Transactional
    public SubcategoriaDTO criar(
            Long usuarioId,
            Long categoriaId,
            SubcategoriaDTO dados) {

        Categoria categoria = buscarCategoria(usuarioId, categoriaId);
        validarNome(categoriaId, dados.nome(), null);

        Subcategoria subcategoria =
                new Subcategoria(dados.nome().trim(), categoria);

        return dto(subcategorias.save(subcategoria));
    }

    @Transactional(readOnly = true)
    public List<SubcategoriaDTO> listar(
            Long usuarioId,
            Long categoriaId) {

        buscarCategoria(usuarioId, categoriaId);

        return subcategorias
                .findAllByCategoriaIdAndCategoriaUsuarioIdOrderByNome(
                        categoriaId, usuarioId)
                .stream()
                .map(this::dto)
                .toList();
    }

    @Transactional
    public SubcategoriaDTO atualizar(
            Long usuarioId,
            Long categoriaId,
            Long id,
            SubcategoriaDTO dados) {

        Subcategoria subcategoria =
                buscar(usuarioId, categoriaId, id);

        validarNome(categoriaId, dados.nome(), subcategoria);
        subcategoria.atualizar(dados.nome().trim());

        return dto(subcategoria);
    }

    @Transactional
    public void remover(
            Long usuarioId,
            Long categoriaId,
            Long id) {

        Subcategoria subcategoria =
                buscar(usuarioId, categoriaId, id);

        if (transacoes.existsBySubcategoriaIdAndUsuarioId(id, usuarioId)) {
            throw new ConflitoException(
                    "A subcategoria possui transações e não pode ser removida.");
        }

        subcategorias.delete(subcategoria);
    }

    private Categoria buscarCategoria(
            Long usuarioId,
            Long categoriaId) {

        return categorias.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Categoria não encontrada."));
    }

    private Subcategoria buscar(
            Long usuarioId,
            Long categoriaId,
            Long id) {

        return subcategorias
                .findByIdAndCategoriaIdAndCategoriaUsuarioId(
                        id, categoriaId, usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subcategoria não encontrada."));
    }

    private void validarNome(
            Long categoriaId,
            String nome,
            Subcategoria atual) {

        boolean existe =
                subcategorias.existsByCategoriaIdAndNomeIgnoreCase(
                        categoriaId, nome.trim());

        if (existe && (atual == null ||
                !atual.getNome().equalsIgnoreCase(nome.trim()))) {
            throw new ConflitoException(
                    "Já existe uma subcategoria com esse nome.");
        }
    }

    private SubcategoriaDTO dto(Subcategoria subcategoria) {
        return new SubcategoriaDTO(
                subcategoria.getId(),
                subcategoria.getNome(),
                subcategoria.getCategoria().getId(),
                subcategoria.getCategoria().getNome()
        );
    }
}