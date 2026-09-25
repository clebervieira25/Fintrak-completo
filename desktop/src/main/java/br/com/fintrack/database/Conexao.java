package br.com.fintrack.database;

import br.com.fintrack.exceptions.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Conexao {
    private static String url = "jdbc:sqlite:fintrack.db";

    private Conexao() {}

    public static Connection abrir() throws SQLException {
        Connection conexao = DriverManager.getConnection(url);
        try (Statement statement = conexao.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return conexao;
    }

    public static void inicializarBanco() {
        String sql = """
                CREATE TABLE IF NOT EXISTS transacoes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data TEXT NOT NULL,
                    descricao TEXT NOT NULL,
                    valor REAL NOT NULL CHECK (valor > 0),
                    tipo TEXT NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
                    observacao TEXT,
                    mensal INTEGER NOT NULL DEFAULT 0
                )
                """;
        try (Connection conexao = abrir(); Statement statement = conexao.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível criar o banco de dados.", e);
        }
    }

    public static void usarUrl(String novaUrl) {
        if (novaUrl == null || novaUrl.isBlank()) throw new IllegalArgumentException("URL JDBC inválida.");
        url = novaUrl;
    }

    public static void restaurarUrlPadrao() {
        url = "jdbc:sqlite:fintrack.db";
    }
}
