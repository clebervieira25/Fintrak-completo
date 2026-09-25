package br.com.fintrack.dao;

import br.com.fintrack.database.Conexao;
import br.com.fintrack.model.TipoTransacao;
import br.com.fintrack.model.Transacao;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoDAOTest {
    private Connection mantenedora;
    private TransacaoDAO dao;

    @BeforeEach
    void prepararBancoEmMemoria() throws SQLException {
        Conexao.usarUrl("jdbc:sqlite:file:fintrack_test?mode=memory&cache=shared");
        mantenedora = Conexao.abrir();
        Conexao.inicializarBanco();
        dao = new TransacaoDAO();
    }

    @AfterEach
    void encerrarBanco() throws SQLException {
        mantenedora.close();
        Conexao.restaurarUrlPadrao();
    }

    @Test
    void deveInserirBuscarEListar() {
        Transacao transacao = nova("Freelance", 600, TipoTransacao.RECEITA);
        dao.inserir(transacao);

        assertNotNull(transacao.getId());
        assertEquals("Freelance", dao.buscarPorId(transacao.getId()).orElseThrow().getDescricao());
        assertEquals(1, dao.listarTodos().size());
    }

    @Test
    void deveAtualizarERemover() {
        Transacao transacao = dao.inserir(nova("Conta", 100, TipoTransacao.DESPESA));
        transacao.setValor(125);

        assertTrue(dao.atualizar(transacao));
        assertEquals(125, dao.buscarPorId(transacao.getId()).orElseThrow().getValor(), 0.001);
        assertTrue(dao.remover(transacao.getId()));
        assertTrue(dao.buscarPorId(transacao.getId()).isEmpty());
    }

    @Test
    void deveCalcularSaldo() {
        dao.inserir(nova("Salário", 2000, TipoTransacao.RECEITA));
        dao.inserir(nova("Mercado", 350, TipoTransacao.DESPESA));
        assertEquals(1650, dao.calcularSaldo(), 0.001);
    }

    private Transacao nova(String descricao, double valor, TipoTransacao tipo) {
        return new Transacao(null, LocalDate.of(2026, 8, 30), descricao, valor, tipo, "", false);
    }
}
