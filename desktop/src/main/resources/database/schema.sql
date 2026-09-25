CREATE TABLE IF NOT EXISTS transacoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    data TEXT NOT NULL,
    descricao TEXT NOT NULL,
    valor REAL NOT NULL CHECK (valor > 0),
    tipo TEXT NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    observacao TEXT,
    mensal INTEGER NOT NULL DEFAULT 0
);
