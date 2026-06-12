package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.dto.motorista.MotoristaRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.motorista.MotoristaResponseDTO;
import gerenciamento.frotas.TrabFinal.service.MotoristaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/motoristas")
public class MotoristaController {

    private final MotoristaService motoristaService;

    public MotoristaController(MotoristaService motoristaService) {
        this.motoristaService = motoristaService;
    }

    @PostMapping
    public ResponseEntity<MotoristaResponseDTO> criar(@Valid @RequestBody MotoristaRequestDTO dto) {
        return ResponseEntity.status(201).body(motoristaService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<MotoristaResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(motoristaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoristaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(motoristaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoristaResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody MotoristaRequestDTO dto) {
        return ResponseEntity.ok(motoristaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        motoristaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MotoristaResponseDTO> atualizarStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(motoristaService.atualizarStatus(id, status));
    }
}