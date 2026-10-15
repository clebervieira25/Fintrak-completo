package br.com.fintrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public final class AuthDTOs {
    private AuthDTOs() {}

    @Schema(name="RegistroRequest", description="Dados para criar um usuário")
    public record RegistroRequest(
            @NotBlank @Size(max=120) String nome,
            @NotBlank @Email @Size(max=180) String email,
            @NotBlank @Size(min=8,max=72) String senha) {}

    @Schema(name="LoginRequest", description="Credenciais de acesso")
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {}

    @Schema(name="TokenResponse", description="JWT e dados básicos do usuário")
    public record TokenResponse(String token, String tipo, Long usuarioId, String nome, String email) {}

    @Schema(name="SolicitarRecuperacaoRequest", description="E-mail cadastrado para recuperação")
    public record SolicitarRecuperacaoRequest(@NotBlank String destino, @NotNull CanalRecuperacao canal) {}

    @Schema(name="ConfirmarRecuperacaoRequest", description="Código recebido e nova senha")
    public record ConfirmarRecuperacaoRequest(
            @NotBlank String destino,
            @NotNull CanalRecuperacao canal,
            @NotBlank @Pattern(regexp="\\d{6}") String codigo,
            @NotBlank @Size(min=8,max=72) String novaSenha) {}

    public enum CanalRecuperacao { SMS, EMAIL }
    public record MensagemResponse(String mensagem) {}
}
