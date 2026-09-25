package br.com.fintrack.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> {
    T inserir(T entidade);
    boolean atualizar(T entidade);
    boolean remover(ID id);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}
