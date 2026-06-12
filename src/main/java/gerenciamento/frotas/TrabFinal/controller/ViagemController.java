package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.dto.viagem.ViagemRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.viagem.ViagemResponseDTO;
import gerenciamento.frotas.TrabFinal.service.ViagemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    private final ViagemService viagemService;

    public ViagemController(ViagemService viagemService) {
        this.viagemService = viagemService;
    }

    @PostMapping
    public ResponseEntity<ViagemResponseDTO> iniciar(
            @Valid @RequestBody ViagemRequestDTO dto,
            @RequestParam(required = false) String estrategia) {
        return ResponseEntity.status(201).body(viagemService.iniciar(dto, estrategia));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ViagemResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(viagemService.finalizar(id));
    }

    @GetMapping
    public ResponseEntity<Page<ViagemResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(viagemService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(viagemService.buscarPorId(id));
    }

    @GetMapping("/ativas")
    public ResponseEntity<Page<ViagemResponseDTO>> listarAtivas(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(viagemService.listarAtivas(pageable));
    }

    @GetMapping("/finalizadas")
    public ResponseEntity<Page<ViagemResponseDTO>> listarFinalizadas(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(viagemService.listarFinalizadas(pageable));
    }
}