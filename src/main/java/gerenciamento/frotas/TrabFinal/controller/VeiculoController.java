package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.dto.veiculo.VeiculoRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.veiculo.VeiculoResponseDTO;
import gerenciamento.frotas.TrabFinal.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> criar(@Valid @RequestBody VeiculoRequestDTO dto) {
        return ResponseEntity.status(201).body(veiculoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<VeiculoResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(veiculoService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody VeiculoRequestDTO dto) {
        return ResponseEntity.ok(veiculoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VeiculoResponseDTO> atualizarStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(veiculoService.atualizarStatus(id, status));
    }
}