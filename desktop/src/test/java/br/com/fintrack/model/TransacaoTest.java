package br.com.fintrack.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {
    @Test
    void receitaDeveTerValorPositivoNoSaldo() {
        Transacao receita = new Transacao(null, LocalDate.now(), "Salário", 2500,
                TipoTransacao.RECEITA, "", true);
        assertEquals(2500, receita.valorComSinal(), 0.001);
    }

    @Test
    void despesaDeveTerValorNegativoNoSaldo() {
        Transacao despesa = new Transacao(null, LocalDate.now(), "Aluguel", 900,
                TipoTransacao.DESPESA, "", true);
        assertEquals(-900, despesa.valorComSinal(), 0.001);
    }

    @Test
    void deveRejeitarValorInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transacao(null, LocalDate.now(), "Teste", 0,
                        TipoTransacao.DESPESA, "", false));
    }
}
