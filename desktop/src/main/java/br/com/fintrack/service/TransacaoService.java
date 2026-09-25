package br.com.fintrack.service;

import br.com.fintrack.dao.TransacaoDAO;
import br.com.fintrack.model.Transacao;
import br.com.fintrack.repository.RepositorioGenerico;

import java.util.List;

public class TransacaoService {
    private final TransacaoDAO dao;

    public TransacaoService(TransacaoDAO dao) {
        this.dao = dao;
    }

    public Transacao salvar(Transacao transacao) {
        if (transacao.getId() == null) return dao.inserir(transacao);
        if (!dao.atualizar(transacao)) throw new IllegalArgumentException("Transação não encontrada.");
        return transacao;
    }

    public List<Transacao> listar() {
        RepositorioGenerico<Transacao> repositorio = new RepositorioGenerico<>();
        repositorio.adicionarTodos(dao.listarTodos()); // ? extends T
        return repositorio.listar();
    }

    public boolean remover(int id) { return dao.remover(id); }
    public double calcularSaldo() { return dao.calcularSaldo(); }
}
