package br.com.fintrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Subcategoria vinculada a uma categoria financeira")
public record SubcategoriaDTO(
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) Long id,
    @NotBlank @Size(max = 80) String nome,
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) Long categoriaId,
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) String categoriaNome
) {}
