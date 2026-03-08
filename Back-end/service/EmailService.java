package br.com.AutoStock.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${autostock.mail.sender}")
    private String senderName;

    @Async
    public void send(String subject, String htmlBody, String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Erro ao enviar e-mail para {}: {}", toEmail, e.getMessage(), e);
        }
    }
    
 // 📎 Novo método — envio com MÚLTIPLOS anexos (ex.: contrato + garantia)
    @Async
    public void sendWithAttachments(
            String subject,
            String htmlBody,
            String toEmail,
            byte[][] attachmentsBytes,   // ex.: { contratoPdf, garantiaPdf }
            String[] fileNames          // ex.: { "Contrato_XYZ.pdf", "Garantia_XYZ.pdf" }
    ) {
        try {
            if (attachmentsBytes == null || fileNames == null || attachmentsBytes.length != fileNames.length) {
                throw new IllegalArgumentException("Quantidade de anexos e nomes de arquivos não confere.");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML

            for (int i = 0; i < attachmentsBytes.length; i++) {
                byte[] bytes = attachmentsBytes[i];
                String name = fileNames[i];

                if (bytes != null && bytes.length > 0) {
                    String finalName = (name != null && !name.isBlank()) ? name : ("anexo-" + (i + 1) + ".pdf");
                    if (!finalName.toLowerCase().endsWith(".pdf")) {
                        finalName = finalName + ".pdf";
                    }
                    helper.addAttachment(finalName, new ByteArrayResource(bytes));
                }
            }

            mailSender.send(message);
            log.info("📧 E-mail com múltiplos anexos enviado para {}", toEmail);

        } catch (Exception e) {
            log.error("❌ Erro ao enviar e-mail com múltiplos anexos para {}: {}", toEmail, e.getMessage(), e);
        }
    }
}

