package br.com.fintrack.dto;

import br.com.fintrack.model.TipoTransacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Transação financeira do usuário autenticado")
public record TransacaoDTO(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) Long id,
    @NotNull LocalDate data,
    @NotBlank @Size(max = 160) String descricao,
    @NotNull @DecimalMin(value = "0.01")
    @Digits(integer = 13, fraction = 2) BigDecimal valor,
    @NotNull TipoTransacao tipo,
    @Size(max = 500) String observacao,
    boolean mensal,
    @NotNull Long categoriaId,
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) String categoriaNome,
    Long subcategoriaId,
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) String subcategoriaNome
) {
    public TransacaoDTO(
            Long id,
            LocalDate data,
            String descricao,
            BigDecimal valor,
            TipoTransacao tipo,
            String observacao,
            boolean mensal,
            Long categoriaId,
            String categoriaNome) {
        this(id, data, descricao, valor, tipo, observacao, mensal,
                categoriaId, categoriaNome, null, null);
    }
}