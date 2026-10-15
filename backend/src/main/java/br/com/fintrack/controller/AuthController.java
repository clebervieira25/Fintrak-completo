package br.com.fintrack.controller;
import br.com.fintrack.dto.AuthDTOs.*;
import br.com.fintrack.service.RecuperacaoService; import br.com.fintrack.service.AuthService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.responses.*; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @Tag(name="Autenticação") public class AuthController{
 private final AuthService service; private final RecuperacaoService recuperacao; public AuthController(AuthService s,RecuperacaoService r){service=s;recuperacao=r;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Registrar usuário") @ApiResponses({@ApiResponse(responseCode="201",description="Criado"),@ApiResponse(responseCode="400",description="Dados inválidos"),@ApiResponse(responseCode="409",description="E-mail já cadastrado")}) public TokenResponse registrar(@Valid @RequestBody RegistroRequest d){return service.registrar(d);}
 @PostMapping("/login") @Operation(summary="Autenticar e obter JWT") @ApiResponses({@ApiResponse(responseCode="200",description="Autenticado"),@ApiResponse(responseCode="401",description="Credenciais inválidas")}) public TokenResponse login(@Valid @RequestBody LoginRequest d){return service.login(d);}
 @PostMapping("/recovery/request") @Operation(summary="Enviar código de recuperação por SMS ou e-mail alternativo") public MensagemResponse solicitarRecuperacao(@Valid @RequestBody SolicitarRecuperacaoRequest d){return recuperacao.solicitar(d);}
 @PostMapping("/recovery/confirm") @Operation(summary="Validar código, redefinir senha e obter JWT") public TokenResponse confirmarRecuperacao(@Valid @RequestBody ConfirmarRecuperacaoRequest d){return recuperacao.confirmar(d);}
}
