package br.com.fintrack.dao;

import br.com.fintrack.database.Conexao;
import br.com.fintrack.exceptions.PersistenciaException;
import br.com.fintrack.model.TipoTransacao;
import br.com.fintrack.model.Transacao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransacaoDAO implements CrudDAO<Transacao, Integer> {
    @Override
    public Transacao inserir(Transacao transacao) {
        String sql = "INSERT INTO transacoes(data, descricao, valor, tipo, observacao, mensal) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.abrir()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preencher(ps, transacao);
                ps.executeUpdate();
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) transacao.setId(chaves.getInt(1));
                }
                conexao.commit();
                return transacao;
            } catch (SQLException e) {
                rollback(conexao);
                throw e;
            }
        } catch (SQLException e) {
            throw erro("inserir", e);
        }
    }

    @Override
    public boolean atualizar(Transacao transacao) {
        if (transacao.getId() == null) throw new IllegalArgumentException("A transação precisa ter ID.");
        String sql = "UPDATE transacoes SET data=?, descricao=?, valor=?, tipo=?, observacao=?, mensal=? WHERE id=?";
        try (Connection conexao = Conexao.abrir(); PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, transacao);
            ps.setInt(7, transacao.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw erro("atualizar", e);
        }
    }

    @Override
    public boolean remover(Integer id) {
        String sql = "DELETE FROM transacoes WHERE id=?";
        try (Connection conexao = Conexao.abrir(); PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw erro("remover", e);
        }
    }

    @Override
    public Optional<Transacao> buscarPorId(Integer id) {
        String sql = "SELECT * FROM transacoes WHERE id=?";
        try (Connection conexao = Conexao.abrir(); PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw erro("buscar", e);
        }
    }

    @Override
    public List<Transacao> listarTodos() {
        String sql = "SELECT * FROM transacoes ORDER BY data DESC, id DESC";
        List<Transacao> resultado = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw erro("listar", e);
        }
    }

    public double calcularSaldo() {
        String sql = "SELECT COALESCE(SUM(CASE WHEN tipo='RECEITA' THEN valor ELSE -valor END), 0) FROM transacoes";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw erro("calcular o saldo", e);
        }
    }

    private void preencher(PreparedStatement ps, Transacao t) throws SQLException {
        ps.setString(1, t.getData().toString());
        ps.setString(2, t.getDescricao());
        ps.setDouble(3, t.getValor());
        ps.setString(4, t.getTipo().name());
        ps.setString(5, t.getObservacao());
        ps.setBoolean(6, t.isMensal());
    }

    private Transacao mapear(ResultSet rs) throws SQLException {
        return new Transacao(rs.getInt("id"), LocalDate.parse(rs.getString("data")),
                rs.getString("descricao"), rs.getDouble("valor"),
                TipoTransacao.valueOf(rs.getString("tipo")), rs.getString("observacao"),
                rs.getBoolean("mensal"));
    }

    private void rollback(Connection conexao) {
        try { conexao.rollback(); } catch (SQLException ignorada) { }
    }

    private PersistenciaException erro(String operacao, SQLException causa) {
        System.err.println("Erro SQL ao " + operacao + ": " + causa.getMessage());
        return new PersistenciaException("Erro ao " + operacao + " transação.", causa);
    }
}
