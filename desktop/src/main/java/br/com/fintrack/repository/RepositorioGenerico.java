package br.com.fintrack.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class RepositorioGenerico<T> {
    private final List<T> registros = new ArrayList<>();

    public void adicionar(T elemento) { registros.add(elemento); }
    public void adicionarTodos(Collection<? extends T> elementos) { registros.addAll(elementos); }
    public boolean remover(T elemento) { return registros.remove(elemento); }
    public boolean removerSe(Predicate<? super T> criterio) { return registros.removeIf(criterio); }
    public List<T> listar() { return List.copyOf(registros); }
    public void copiarPara(Collection<? super T> destino) { destino.addAll(registros); }
    public int tamanho() { return registros.size(); }
}
