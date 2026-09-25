package br.com.fintrack.dto;
import io.swagger.v3.oas.annotations.media.Schema; import java.math.BigDecimal; import java.time.LocalDate;
@Schema(description="Resumo financeiro do usuário no período")
public record SaldoDTO(BigDecimal totalReceitas,BigDecimal totalDespesas,BigDecimal saldo,LocalDate inicio,LocalDate fim){}
