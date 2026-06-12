package gerenciamento.frotas.TrabFinal.service;

import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Veiculo;
import gerenciamento.frotas.TrabFinal.model.entity.Motorista;
import gerenciamento.frotas.TrabFinal.repository.MotoristaRepository;
import gerenciamento.frotas.TrabFinal.repository.VeiculoRepository;
import gerenciamento.frotas.TrabFinal.repository.ViagemRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioExcelService {

    private final ViagemRepository viagemRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RelatorioExcelService(ViagemRepository viagemRepository,
                                 VeiculoRepository veiculoRepository,
                                 MotoristaRepository motoristaRepository) {
        this.viagemRepository = viagemRepository;
        this.veiculoRepository = veiculoRepository;
        this.motoristaRepository = motoristaRepository;
    }

    public byte[] gerarRelatorioViagens() throws Exception {
        List<Viagem> viagens = viagemRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Viagens");

            // Estilo cabeçalho
            CellStyle cabStyle = workbook.createCellStyle();
            cabStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            cabStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font cabFont = workbook.createFont();
            cabFont.setBold(true);
            cabFont.setColor(IndexedColors.WHITE.getIndex());
            cabStyle.setFont(cabFont);

            // Cabeçalho
            Row cab = sheet.createRow(0);
            String[] colunas = {"ID", "Veículo (Placa)", "Motorista", "Origem",
                    "Destino", "Distância (km)", "Partida",
                    "Custo (R$)", "Status"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = cab.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(cabStyle);
            }

            // Dados
            int rowNum = 1;
            for (Viagem v : viagens) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getVeiculo().getPlaca());
                row.createCell(2).setCellValue(v.getMotorista().getNome());
                row.createCell(3).setCellValue(v.getRota().getOrigem());
                row.createCell(4).setCellValue(v.getRota().getDestino());
                row.createCell(5).setCellValue(v.getRota().getDistanciaKm());
                row.createCell(6).setCellValue(
                        v.getDataPartida() != null ? v.getDataPartida().format(FMT) : "");
                row.createCell(7).setCellValue(
                        v.getCustoCalculado() != null ? v.getCustoCalculado() : 0.0);
                row.createCell(8).setCellValue(
                        v.getStatusViagem() ? "Finalizada" : "Em andamento");
            }

            // Auto-size
            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);

            // Aba de resumo
            Sheet resumo = workbook.createSheet("Resumo");
            resumo.createRow(0).createCell(0).setCellValue("Total de Viagens");
            resumo.getRow(0).createCell(1).setCellValue(viagens.size());
            resumo.createRow(1).createCell(0).setCellValue("Finalizadas");
            resumo.getRow(1).createCell(1)
                    .setCellValue(viagens.stream().filter(Viagem::getStatusViagem).count());
            resumo.createRow(2).createCell(0).setCellValue("Custo Total (R$)");
            resumo.getRow(2).createCell(1).setCellValue(
                    viagens.stream().filter(v -> v.getCustoCalculado() != null)
                            .mapToDouble(Viagem::getCustoCalculado).sum());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] gerarRelatorioVeiculos() throws Exception {
        List<Veiculo> veiculos = veiculoRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Veículos");

            CellStyle cabStyle = workbook.createCellStyle();
            cabStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            cabStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font cabFont = workbook.createFont();
            cabFont.setBold(true);
            cabFont.setColor(IndexedColors.WHITE.getIndex());
            cabStyle.setFont(cabFont);

            Row cab = sheet.createRow(0);
            String[] colunas = {"ID", "Placa", "Tipo", "Chassi", "Ano",
                    "Capacidade (kg)", "Status"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = cab.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(cabStyle);
            }

            int rowNum = 1;
            for (Veiculo v : veiculos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getPlaca());
                row.createCell(2).setCellValue(v.getTipo());
                row.createCell(3).setCellValue(v.getChassi());
                row.createCell(4).setCellValue(v.getAno());
                row.createCell(5).setCellValue(v.getCapacidadeKg());
                row.createCell(6).setCellValue(v.getStatus().name());
            }

            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] gerarRelatorioMotoristas() throws Exception {
        List<Motorista> motoristas = motoristaRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Motoristas");

            CellStyle cabStyle = workbook.createCellStyle();
            cabStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            cabStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font cabFont = workbook.createFont();
            cabFont.setBold(true);
            cabFont.setColor(IndexedColors.WHITE.getIndex());
            cabStyle.setFont(cabFont);

            Row cab = sheet.createRow(0);
            String[] colunas = {"ID", "Nome", "CPF", "CNH", "Categoria CNH",
                    "Telefone", "Status"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = cab.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(cabStyle);
            }

            int rowNum = 1;
            for (Motorista m : motoristas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getId());
                row.createCell(1).setCellValue(m.getNome());
                row.createCell(2).setCellValue(m.getCpf());
                row.createCell(3).setCellValue(m.getCnh());
                row.createCell(4).setCellValue(m.getCategoriaCnh());
                row.createCell(5).setCellValue(m.getTelefone());
                row.createCell(6).setCellValue(m.getStatus().name());
            }

            for (int i = 0; i < colunas.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}