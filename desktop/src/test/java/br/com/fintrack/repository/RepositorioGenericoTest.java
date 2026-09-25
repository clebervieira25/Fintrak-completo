package br.com.fintrack.repository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioGenericoTest {
    @Test
    void deveAdicionarListarERemoverElementos() {
        RepositorioGenerico<Number> repositorio = new RepositorioGenerico<>();
        List<Integer> inteiros = List.of(10, 20, 30);

        repositorio.adicionarTodos(inteiros); // Collection<? extends Number>
        List<Object> destino = new ArrayList<>();
        repositorio.copiarPara(destino);      // Collection<? super Number>
        boolean removeu = repositorio.removerSe(numero -> numero.intValue() == 20);

        assertTrue(removeu);
        assertEquals(List.of(10, 30), repositorio.listar());
        assertEquals(List.of(10, 20, 30), destino);
    }
}
