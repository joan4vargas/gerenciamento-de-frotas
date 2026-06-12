package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.config.JwtUtil;
import gerenciamento.frotas.TrabFinal.dto.usuario.LoginRequestDTO;
import gerenciamento.frotas.TrabFinal.exception.BusinessException;
import gerenciamento.frotas.TrabFinal.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO dto) {
        var usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        String token = jwtUtil.gerarToken(usuario.getEmail(), usuario.getTipo());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", usuario.getTipo(),
                "nome", usuario.getNome(),
                "id", usuario.getId().toString()
        ));
    }
}