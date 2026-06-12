package gerenciamento.frotas.TrabFinal.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import gerenciamento.frotas.TrabFinal.model.entity.Viagem;
import gerenciamento.frotas.TrabFinal.model.entity.veiculo.Veiculo;
import gerenciamento.frotas.TrabFinal.model.entity.Motorista;
import gerenciamento.frotas.TrabFinal.repository.MotoristaRepository;
import gerenciamento.frotas.TrabFinal.repository.VeiculoRepository;
import gerenciamento.frotas.TrabFinal.repository.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioPdfService {

    private final ViagemRepository viagemRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RelatorioPdfService(ViagemRepository viagemRepository,
                               VeiculoRepository veiculoRepository,
                               MotoristaRepository motoristaRepository) {
        this.viagemRepository = viagemRepository;
        this.veiculoRepository = veiculoRepository;
        this.motoristaRepository = motoristaRepository;
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioViagens() throws Exception {
        List<Viagem> viagens = viagemRepository.findAll();
        Document doc = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph titulo = new Paragraph("Relatório de Viagens - Sistema de Frotas", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        doc.add(titulo);

        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph data = new Paragraph("Gerado em: " +
                java.time.LocalDateTime.now().format(FMT), subFont);
        data.setAlignment(Element.ALIGN_RIGHT);
        data.setSpacingAfter(15);
        doc.add(data);

        PdfPTable tabela = new PdfPTable(7);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1f, 2f, 2f, 2f, 2f, 1.5f, 1.5f});

        String[] cabecalhos = {"ID", "Veículo", "Motorista", "Origem", "Destino",
                "Custo (R$)", "Status"};
        Font cabFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        for (String cab : cabecalhos) {
            PdfPCell cell = new PdfPCell(new Phrase(cab, cabFont));
            cell.setBackgroundColor(new BaseColor(46, 125, 50));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            tabela.addCell(cell);
        }

        Font dadoFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        boolean linha = false;
        for (Viagem v : viagens) {
            BaseColor cor = linha ? new BaseColor(232, 245, 233) : BaseColor.WHITE;
            addCell(tabela, String.valueOf(v.getId()), dadoFont, cor);
            addCell(tabela, v.getVeiculo().getPlaca(), dadoFont, cor);
            addCell(tabela, v.getMotorista().getNome(), dadoFont, cor);
            addCell(tabela, v.getRota().getOrigem(), dadoFont, cor);
            addCell(tabela, v.getRota().getDestino(), dadoFont, cor);
            addCell(tabela, v.getCustoCalculado() != null ?
                    String.format("%.2f", v.getCustoCalculado()) : "-", dadoFont, cor);
            addCell(tabela, v.getStatusViagem() ? "Finalizada" : "Em andamento", dadoFont, cor);
            linha = !linha;
        }

        doc.add(tabela);

        double totalCusto = viagens.stream()
                .filter(v -> v.getCustoCalculado() != null)
                .mapToDouble(Viagem::getCustoCalculado).sum();
        long totalFinalizadas = viagens.stream().filter(Viagem::getStatusViagem).count();

        doc.add(new Paragraph(" "));
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        doc.add(new Paragraph("Total de viagens: " + viagens.size(), totalFont));
        doc.add(new Paragraph("Viagens finalizadas: " + totalFinalizadas, totalFont));
        doc.add(new Paragraph(String.format("Custo total: R$ %.2f", totalCusto), totalFont));

        doc.close();
        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioVeiculos() throws Exception {
        List<Veiculo> veiculos = veiculoRepository.findAll();
        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph titulo = new Paragraph("Relatório de Veículos - Sistema de Frotas", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        doc.add(titulo);

        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph dataGeracao = new Paragraph("Gerado em: " +
                java.time.LocalDateTime.now().format(FMT), subFont);
        dataGeracao.setAlignment(Element.ALIGN_RIGHT);
        dataGeracao.setSpacingAfter(15);
        doc.add(dataGeracao);

        PdfPTable tabela = new PdfPTable(5);
        tabela.setWidthPercentage(100);

        String[] cabecalhos = {"ID", "Placa", "Tipo", "Capacidade (kg)", "Status"};
        Font cabFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        for (String cab : cabecalhos) {
            PdfPCell cell = new PdfPCell(new Phrase(cab, cabFont));
            cell.setBackgroundColor(new BaseColor(46, 125, 50));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            tabela.addCell(cell);
        }

        Font dadoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        boolean linha = false;
        for (Veiculo v : veiculos) {
            BaseColor cor = linha ? new BaseColor(232, 245, 233) : BaseColor.WHITE;
            addCell(tabela, String.valueOf(v.getId()), dadoFont, cor);
            addCell(tabela, v.getPlaca(), dadoFont, cor);
            addCell(tabela, v.getTipo(), dadoFont, cor);
            addCell(tabela, String.valueOf(v.getCapacidadeKg()), dadoFont, cor);
            addCell(tabela, v.getStatus().name(), dadoFont, cor);
            linha = !linha;
        }

        doc.add(tabela);

        doc.add(new Paragraph(" "));
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        doc.add(new Paragraph("Total de veículos: " + veiculos.size(), totalFont));

        doc.close();
        return out.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioMotoristas() throws Exception {
        List<Motorista> motoristas = motoristaRepository.findAll();
        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph titulo = new Paragraph("Relatório de Motoristas - Sistema de Frotas", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        doc.add(titulo);

        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph dataGeracao = new Paragraph("Gerado em: " +
                java.time.LocalDateTime.now().format(FMT), subFont);
        dataGeracao.setAlignment(Element.ALIGN_RIGHT);
        dataGeracao.setSpacingAfter(15);
        doc.add(dataGeracao);

        PdfPTable tabela = new PdfPTable(5);
        tabela.setWidthPercentage(100);

        String[] cabecalhos = {"ID", "Nome", "CPF", "CNH", "Status"};
        Font cabFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        for (String cab : cabecalhos) {
            PdfPCell cell = new PdfPCell(new Phrase(cab, cabFont));
            cell.setBackgroundColor(new BaseColor(46, 125, 50));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            tabela.addCell(cell);
        }

        Font dadoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        boolean linha = false;
        for (Motorista m : motoristas) {
            BaseColor cor = linha ? new BaseColor(232, 245, 233) : BaseColor.WHITE;
            addCell(tabela, String.valueOf(m.getId()), dadoFont, cor);
            addCell(tabela, m.getNome(), dadoFont, cor);
            addCell(tabela, m.getCpf(), dadoFont, cor);
            addCell(tabela, m.getCnh(), dadoFont, cor);
            addCell(tabela, m.getStatus().name(), dadoFont, cor);
            linha = !linha;
        }

        doc.add(tabela);

        doc.add(new Paragraph(" "));
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        doc.add(new Paragraph("Total de motoristas: " + motoristas.size(), totalFont));

        doc.close();
        return out.toByteArray();
    }

    private void addCell(PdfPTable tabela, String texto, Font font, BaseColor cor) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", font));
        cell.setBackgroundColor(cor);
        cell.setPadding(5);
        tabela.addCell(cell);
    }
}