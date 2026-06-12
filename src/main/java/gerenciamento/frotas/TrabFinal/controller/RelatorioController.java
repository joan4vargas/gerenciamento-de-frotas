package gerenciamento.frotas.TrabFinal.controller;

import gerenciamento.frotas.TrabFinal.service.RelatorioExcelService;
import gerenciamento.frotas.TrabFinal.service.RelatorioPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioPdfService pdfService;
    private final RelatorioExcelService excelService;

    public RelatorioController(RelatorioPdfService pdfService,
                               RelatorioExcelService excelService) {
        this.pdfService = pdfService;
        this.excelService = excelService;
    }

    // ─── PDF ───────────────────────────────────────────

    @GetMapping("/viagens/pdf")
    public ResponseEntity<byte[]> viagensPdf() throws Exception {
        byte[] pdf = pdfService.gerarRelatorioViagens();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-viagens.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/veiculos/pdf")
    public ResponseEntity<byte[]> veiculosPdf() throws Exception {
        byte[] pdf = pdfService.gerarRelatorioVeiculos();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-veiculos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/motoristas/pdf")
    public ResponseEntity<byte[]> motoristasPdf() throws Exception {
        byte[] pdf = pdfService.gerarRelatorioMotoristas();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-motoristas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ─── EXCEL ─────────────────────────────────────────

    @GetMapping("/viagens/excel")
    public ResponseEntity<byte[]> viagensExcel() throws Exception {
        byte[] excel = excelService.gerarRelatorioViagens();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-viagens.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/veiculos/excel")
    public ResponseEntity<byte[]> veiculosExcel() throws Exception {
        byte[] excel = excelService.gerarRelatorioVeiculos();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-veiculos.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @GetMapping("/motoristas/excel")
    public ResponseEntity<byte[]> motoristasExcel() throws Exception {
        byte[] excel = excelService.gerarRelatorioMotoristas();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-motoristas.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}