package br.com.fintrack.service;

import br.com.fintrack.dto.AuthDTOs.*;
import br.com.fintrack.exception.ValidationException;
import br.com.fintrack.model.*;
import br.com.fintrack.repository.*;
import br.com.fintrack.security.UsuarioPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.*;
import java.util.Optional;

@Service
public class RecuperacaoService {
    private final UsuarioRepository usuarios;
    private final CodigoRecuperacaoRepository codigos;
    private final PasswordEncoder encoder;
    private final EnvioRecuperacaoService envio;
    private final AuthService auth;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.recovery.expiration:PT10M}")
    private Duration duracao;

    public RecuperacaoService(UsuarioRepository usuarios, CodigoRecuperacaoRepository codigos,
                              PasswordEncoder encoder, EnvioRecuperacaoService envio, AuthService auth) {
        this.usuarios = usuarios;
        this.codigos = codigos;
        this.encoder = encoder;
        this.envio = envio;
        this.auth = auth;
    }

    @Transactional
    public MensagemResponse solicitar(SolicitarRecuperacaoRequest pedido) {
        Optional<Usuario> encontrado = buscar(pedido.destino().trim(), pedido.canal());
        if (encontrado.isPresent()) {
            Usuario usuario = encontrado.get();
            String codigo = String.format("%06d", random.nextInt(1_000_000));
            codigos.deleteByUsuarioId(usuario.getId());
            codigos.save(new CodigoRecuperacao(usuario, encoder.encode(codigo), Instant.now().plus(duracao)));

            if (pedido.canal() == CanalRecuperacao.SMS) {
                envio.enviarSms(usuario.getTelefone(), codigo);
            } else {
                envio.enviarEmail(usuario.getEmail(), codigo);
            }
        }
        return new MensagemResponse("Se o e-mail estiver cadastrado, o código será enviado.");
    }

    @Transactional
    public TokenResponse confirmar(ConfirmarRecuperacaoRequest pedido) {
        Usuario usuario = buscar(pedido.destino().trim(), pedido.canal()).orElseThrow(this::codigoInvalido);
        CodigoRecuperacao codigo = codigos.findFirstByUsuarioIdAndUsadoFalseOrderByIdDesc(usuario.getId())
                .orElseThrow(this::codigoInvalido);
        if (codigo.expirado() || codigo.getTentativas() >= 5) throw codigoInvalido();
        if (!encoder.matches(pedido.codigo(), codigo.getCodigoHash())) {
            codigo.registrarTentativa();
            throw codigoInvalido();
        }
        codigo.marcarUsado();
        usuario.alterarSenha(encoder.encode(pedido.novaSenha()));
        return auth.token(UsuarioPrincipal.de(usuario));
    }

    private Optional<Usuario> buscar(String destino, CanalRecuperacao canal) {
        return canal == CanalRecuperacao.SMS
                ? usuarios.findByTelefone(destino)
                : usuarios.findByEmailIgnoreCase(destino);
    }

    private ValidationException codigoInvalido() {
        return new ValidationException("Código inválido ou expirado.");
    }
}
