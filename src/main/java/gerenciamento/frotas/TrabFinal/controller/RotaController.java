package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.dto.rota.RotaRequestDTO;
import gerenciamento.frotas.TrabFinal.dto.rota.RotaResponseDTO;
import gerenciamento.frotas.TrabFinal.service.RotaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rotas")
public class RotaController {

    private final RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    @PostMapping
    public ResponseEntity<RotaResponseDTO> criar(@Valid @RequestBody RotaRequestDTO dto) {
        return ResponseEntity.status(201).body(rotaService.criar(dto));
    }

    @PostMapping("/gps")
    public ResponseEntity<RotaResponseDTO> criarComGps(
            @RequestParam String origem,
            @RequestParam String destino) {
        return ResponseEntity.status(201).body(rotaService.criarComGps(origem, destino));
    }

    // Retorna info da rota (distância, tempo, provedor)
    @GetMapping("/gps/consultar")
    public ResponseEntity<Map<String, Object>> consultarGps(
            @RequestParam String origem,
            @RequestParam String destino) {
        return ResponseEntity.ok(rotaService.obterRotaGps(origem, destino));
    }

    // Retorna geometria real da rota (lista de coordenadas [lat, lon])
    @GetMapping("/gps/geometria")
    public ResponseEntity<List<double[]>> obterGeometria(
            @RequestParam String origem,
            @RequestParam String destino) {
        return ResponseEntity.ok(rotaService.obterGeometriaRota(origem, destino));
    }

    @GetMapping
    public ResponseEntity<Page<RotaResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(rotaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RotaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rotaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RotaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RotaRequestDTO dto) {
        return ResponseEntity.ok(rotaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        rotaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}