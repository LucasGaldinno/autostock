package br.com.AutoStock.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import br.com.AutoStock.model.Warranty;
import br.com.AutoStock.model.Vehicle;
import br.com.AutoStock.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarrantyPdfService {

    public byte[] generateWarrantyPdf(Warranty warranty) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Paragraph title = new Paragraph("TERMO DE GARANTIA VEICULAR", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Warranty w = warranty;
            Vehicle v = w.getVehicle();
            User agency = w.getUser();

            // ===== DADOS DA GARANTIA =====
            document.add(new Paragraph("INFORMAÇÕES DA GARANTIA", sectionFont));
            document.add(new Paragraph("Início: " + w.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), textFont));
            document.add(new Paragraph("Término: " + w.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), textFont));
            document.add(new Paragraph("Descrição: " + (w.getDescription() != null ? w.getDescription() : "Garantia padrão de 3 meses."), textFont));
            document.add(new Paragraph(" "));

            // ===== DADOS DO VEÍCULO =====
            document.add(new Paragraph("VEÍCULO COBERTO PELA GARANTIA", sectionFont));
            document.add(new Paragraph("Marca: " + v.getBrand(), textFont));
            document.add(new Paragraph("Modelo: " + v.getModel(), textFont));
            document.add(new Paragraph("Versão: " + v.getVersion(), textFont));
            document.add(new Paragraph("Ano: " + (v.getManufactureYear() + "/" + v.getModelYear()), textFont));
            document.add(new Paragraph("Placa: " + v.getPlate(), textFont));
            document.add(new Paragraph("Chassi: " + v.getChassis(), textFont));
            document.add(new Paragraph(" "));

            // ===== AGÊNCIA RESPONSÁVEL =====
            document.add(new Paragraph("AGÊNCIA RESPONSÁVEL PELA GARANTIA", sectionFont));
            String nomeAgencia = (agency.getNomeFantasia() != null && !agency.getNomeFantasia().isBlank())
                    ? agency.getNomeFantasia()
                    : agency.getRazaoSocial();
            document.add(new Paragraph("Razão Social: " + nomeAgencia, textFont));
            document.add(new Paragraph("CNPJ: " + agency.getCnpj(), textFont));
            document.add(new Paragraph("Telefone: " + agency.getTelefone(), textFont));
            document.add(new Paragraph("E-mail: " + agency.getEmail(), textFont));
            document.add(new Paragraph(" "));

            // ===== TERMOS PADRÃO =====
            document.add(new Paragraph("TERMOS E CONDIÇÕES DA GARANTIA", sectionFont));
            document.add(new Paragraph("""
                    1. Esta garantia cobre defeitos mecânicos e elétricos de fabricação durante o período indicado.
                    2. A garantia não cobre desgaste natural, mau uso, acidentes, modificações não autorizadas ou falta de manutenção.
                    3. O cliente deve realizar manutenções preventivas conforme o manual do fabricante.
                    4. Qualquer intervenção fora de oficinas autorizadas pela agência implica na perda da garantia.
                    5. Este termo é pessoal e intransferível.
                    """, textFont));
            document.add(new Paragraph(" "));

            Paragraph footer = new Paragraph("Gerado automaticamente pelo sistema AutoStock © 2025",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar termo de garantia: " + e.getMessage(), e);
        }
    }
}
