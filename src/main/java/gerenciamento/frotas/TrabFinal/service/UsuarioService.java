package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.dto.usuario.UsuarioRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.usuario.UsuarioResponseDTO;
import gerenciamento.frotas.TrabFinal.exception.BusinessException;
import gerenciamento.frotas.TrabFinal.exception.ResourceNotFoundException;
import gerenciamento.frotas.TrabFinal.model.entity.Usuario;
import gerenciamento.frotas.TrabFinal.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail()))
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());

        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setSenha(passwordEncoder.encode(dto.getSenha()));
        u.setTipo(dto.getTipo() != null ? dto.getTipo() : "ROLE_USER");
        return toDTO(usuarioRepository.save(u));
    }

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id)));
    }

    @Transactional
    public void deletar(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO toDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNome(u.getNome());
        dto.setEmail(u.getEmail());
        dto.setTipo(u.getTipo());
        return dto;
    }
}