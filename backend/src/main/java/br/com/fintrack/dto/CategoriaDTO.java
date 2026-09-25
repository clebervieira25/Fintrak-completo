package br.com.fintrack.dto;
import io.swagger.v3.oas.annotations.media.Schema; import jakarta.validation.constraints.*;
@Schema(description="Categoria personalizada pertencente ao usuário autenticado")
public record CategoriaDTO(@Schema(accessMode=Schema.AccessMode.READ_ONLY) Long id,@NotBlank @Size(max=80) String nome,@Pattern(regexp="^#[0-9A-Fa-f]{6}$",message="A cor deve estar no formato #RRGGBB") String cor){}
