package br.com.AutoStock.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import br.com.AutoStock.model.SaleContract;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.Vehicle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractPdfService {

    public byte[] generateContractPdf(SaleContract c, byte[] assinaturaAgencia, byte[] assinaturaCliente) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // === Fontes padrão ===
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // ===== TÍTULO =====
            Paragraph title = new Paragraph("CONTRATO DE COMPRA E VENDA DE VEÍCULO", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // ===== IDENTIFICAÇÃO DAS PARTES =====
            document.add(new Paragraph("IDENTIFICAÇÃO DAS PARTES", sectionFont));
            document.add(new Paragraph(" "));

            // ===== AGÊNCIA VENDEDORA =====
            document.add(new Paragraph("AGÊNCIA VENDEDORA:", sectionFont));

            User agency = c.getUser();

            // Usa nome fantasia se existir, senão razão social
            String nomeAgencia = (agency.getNomeFantasia() != null && !agency.getNomeFantasia().isBlank())
                    ? agency.getNomeFantasia()
                    : agency.getRazaoSocial();

            document.add(new Paragraph("Razão Social: " + safe(nomeAgencia), textFont));
            document.add(new Paragraph("CNPJ: " + formatDocument(agency.getCnpj(), "CNPJ"), textFont));
            document.add(new Paragraph("Endereço: " + safe(agency.getLogradouro()), textFont));
            document.add(new Paragraph("Número: " + safe(agency.getNumero()), textFont));

            if (agency.getComplemento() != null && !agency.getComplemento().isBlank()) {
                document.add(new Paragraph("Complemento: " + safe(agency.getComplemento()), textFont));
            }

            document.add(new Paragraph("Bairro: " + safe(agency.getBairro()), textFont));
            document.add(new Paragraph("Cidade: " + safe(agency.getCidade()), textFont));
            document.add(new Paragraph("UF: " + safe(agency.getUf()), textFont));
            document.add(new Paragraph("CEP: " + formatDocument(agency.getCep(), "CEP"), textFont));
            document.add(new Paragraph("Telefone: " + safe(agency.getTelefone()), textFont));
            document.add(new Paragraph("E-mail: " + safe(agency.getEmail()), textFont));
            document.add(new Paragraph(" "));

            // ===== CLIENTE COMPRADOR =====
            document.add(new Paragraph("CLIENTE COMPRADOR:", sectionFont));
            document.add(new Paragraph("Nome: " + safe(c.getCustomerName()), textFont));
            document.add(new Paragraph("CPF: " + formatDocument(c.getCpf(), "CPF"), textFont));
            document.add(new Paragraph("Telefone: " + safe(c.getCustomerPhone()), textFont));
            document.add(new Paragraph("E-mail: " + safe(c.getCustomerEmail()), textFont));
            document.add(new Paragraph("Endereço: " + safe(c.getAddress()), textFont));
            document.add(new Paragraph("Número: " + safe(c.getNumber()), textFont));
            if (c.getComplement() != null && !c.getComplement().isBlank()) {
                document.add(new Paragraph("Complemento: " + safe(c.getComplement()), textFont));
            }
            document.add(new Paragraph("Bairro: " + safe(c.getNeighborhood()), textFont));
            document.add(new Paragraph("Cidade: " + safe(c.getCity()), textFont));
            document.add(new Paragraph("UF: " + safe(c.getState()), textFont));
            document.add(new Paragraph("CEP: " + formatDocument(c.getCep(), "CEP"), textFont));
            document.add(new Paragraph(" "));

            // ===== DADOS DO VEÍCULO =====
            document.add(new Paragraph("DADOS DO VEÍCULO", sectionFont));

            Vehicle v = c.getVehicle();

            document.add(new Paragraph("Marca: " + safe(v.getBrand()), textFont));
            document.add(new Paragraph("Modelo: " + safe(v.getModel()), textFont));
            document.add(new Paragraph("Versão: " + safe(v.getVersion()), textFont));

            String yearModel = "";
            if (v.getManufactureYear() != null && v.getModelYear() != null) {
                yearModel = v.getManufactureYear() + "/" + v.getModelYear();
            } else if (v.getModelYear() != null) {
                yearModel = v.getModelYear().toString();
            } else if (v.getManufactureYear() != null) {
                yearModel = v.getManufactureYear().toString();
            }
            document.add(new Paragraph("Ano/Fabricação-Modelo: " + safe(yearModel), textFont));

            document.add(new Paragraph("Cor: " + (v.getColor() != null ? safe(v.getColor().getLabel()) : ""), textFont));
            document.add(new Paragraph("Placa: " + safe(v.getPlate()), textFont));
            document.add(new Paragraph("RENAVAM: " + safe(v.getRenavam()), textFont));
            document.add(new Paragraph("Chassi: " + safe(v.getChassis()), textFont));

            // === Quilometragem ===
            String kmFormatado = "";
            if (v.getMileage() != null) {
                try {
                    kmFormatado = String.format("%,d", v.getMileage()).replace(',', '.');
                } catch (Exception e) {
                    kmFormatado = v.getMileage().toString();
                }
            }
            document.add(new Paragraph("Quilometragem: " + kmFormatado + " km", textFont));

            // === Preço de venda ===
            java.text.NumberFormat moedaBR = java.text.NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            String precoFormatado = (c.getSalePrice() != null)
                    ? moedaBR.format(c.getSalePrice())
                    : "R$ 0,00";
            document.add(new Paragraph("Preço de venda: " + precoFormatado, textFont));

            // === Tipo de negociação ===
            document.add(new Paragraph("Tipo de negociação: " + safe(c.getNegotiationType()), textFont));
            document.add(new Paragraph(" "));

            // ===== CLÁUSULAS =====
            document.add(new Paragraph("CLÁUSULAS CONTRATUAIS", sectionFont));
            document.add(new Paragraph(
                    "1. O presente contrato tem por objeto a compra e venda do veículo acima descrito, conforme as condições acordadas entre as partes.",
                    textFont));
            document.add(new Paragraph(
                    "2. O comprador declara ter examinado o veículo e estar ciente de seu estado de conservação.",
                    textFont));
            document.add(new Paragraph(
                    "3. A agência declara que o veículo está livre de quaisquer ônus, débitos ou restrições legais.",
                    textFont));
            document.add(new Paragraph(
                    "4. O pagamento será efetuado conforme a forma de negociação escolhida pelo comprador.", textFont));
            document.add(new Paragraph(
                    "5. Ambas as partes declaram estar de comum acordo com os termos aqui descritos.", textFont));
            document.add(new Paragraph(" "));

            // ===== DATA E LOCAL =====
            Locale localeBR = new Locale("pt", "BR");
            String dataFormatada = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", localeBR));

            document.add(new Paragraph(
                    "Local: " + safe(agency.getCidade()) + " - " + safe(agency.getUf()),
                    textFont));
            document.add(new Paragraph("Data: " + dataFormatada, textFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // ===== ASSINATURAS =====
            document.add(new Paragraph("ASSINATURAS DIGITAIS", sectionFont));
            document.add(new Paragraph(" "));

            String agencyDisplayName = nomeAgencia;

            // ==== Assinatura da Agência ====
            try {
                document.add(new Paragraph("Agência: " + safe(agencyDisplayName), textFont));

                if (assinaturaAgencia != null && assinaturaAgencia.length > 0) {
                    log.info("Gerando assinatura da agência (imagem PNG, {} bytes)", assinaturaAgencia.length);
                    Image assinatura = Image.getInstance(assinaturaAgencia);
                    assinatura.scaleToFit(120, 60);
                    assinatura.setAlignment(Element.ALIGN_LEFT);
                    document.add(assinatura);
                } else {
                    log.warn("Nenhuma assinatura PNG enviada pela agência; tentando imagem padrão.");
                    var resource = new ClassPathResource("static/img/signatures/agencia.png");
                    if (resource.exists()) {
                        Image assinatura = Image.getInstance(resource.getInputStream().readAllBytes());
                        assinatura.scaleToFit(120, 60);
                        assinatura.setAlignment(Element.ALIGN_LEFT);
                        document.add(assinatura);
                    } else {
                        String dataAssinatura = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        document.add(new Paragraph("────────────────────────────────────────────", textFont));
                        document.add(new Paragraph("Assinado digitalmente por " + safe(agencyDisplayName), textFont));
                        document.add(new Paragraph("Data: " + dataAssinatura, textFont));
                        document.add(new Paragraph("────────────────────────────────────────────", textFont));
                    }
                }
            } catch (Exception e) {
                log.error("Erro ao adicionar assinatura da agência: {}", e.getMessage(), e);
                document.add(new Paragraph("Assinatura da agência não disponível.", textFont));
            }

            document.add(new Paragraph(" "));

            // ==== Assinatura do Cliente ====
            try {
                document.add(new Paragraph("Cliente: " + safe(c.getCustomerName()), textFont));

                if (assinaturaCliente != null && assinaturaCliente.length > 0) {
                    log.info("Gerando assinatura do cliente (imagem PNG, {} bytes)", assinaturaCliente.length);
                    Image assinatura = Image.getInstance(assinaturaCliente);
                    assinatura.scaleToFit(120, 60);
                    assinatura.setAlignment(Element.ALIGN_LEFT);
                    document.add(assinatura);
                } else {
                    String dataAssinatura = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    document.add(new Paragraph("────────────────────────────────────────────", textFont));
                    document.add(new Paragraph("Assinado digitalmente por " + safe(c.getCustomerName()), textFont));
                    document.add(new Paragraph("Data: " + dataAssinatura, textFont));
                    document.add(new Paragraph("────────────────────────────────────────────", textFont));
                }
            } catch (Exception e) {
                log.error("Erro ao adicionar assinatura do cliente: {}", e.getMessage(), e);
                document.add(new Paragraph("Assinatura do cliente não disponível.", textFont));
            }

            // ===== RODAPÉ =====
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Gerado automaticamente pelo sistema AutoStock © 2025",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar contrato PDF: " + e.getMessage(), e);
        }
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String formatDocument(String value, String type) {
        if (value == null || value.isBlank()) return "";

        value = value.replaceAll("\\D", ""); // remove tudo que não for número

        switch (type) {
            case "CPF":
                if (value.length() == 11)
                    return value.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
                break;
            case "CNPJ":
                if (value.length() == 14)
                    return value.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
                break;
            case "CEP":
                if (value.length() == 8)
                    return value.replaceFirst("(\\d{5})(\\d{3})", "$1-$2");
                break;
        }
        return value;
    }
}
