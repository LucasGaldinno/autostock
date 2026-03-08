package br.com.AutoStock.event;

import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.AutoStock.model.User;
import br.com.AutoStock.service.EmailService;
import br.com.AutoStock.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

	private final VerificationTokenService tokenService;
	private final EmailService emailService;

	@Override
	public void onApplicationEvent(RegistrationCompleteEvent event) {
		User user = event.getUser();
		String vToken = UUID.randomUUID().toString();
		tokenService.saveVerificationTokenForUser(user, vToken);

		String url = event.getConfirmationUrl() + "/registration/verifyEmail?token=" + vToken;

		sendVerificationEmail(user, url);
	}

	private String buildEmailTemplate(String title, String greeting, String message, String actionText,
			String actionUrl, String footer) {
		String buttonHtml = "";
		if (actionText != null && actionUrl != null) {
			buttonHtml = "<div style=\"text-align:center; margin: 30px 0;\">" + "<a href=\"" + actionUrl + "\" "
					+ "style=\"background-color:#005aa7; color:white; padding:12px 24px; "
					+ "text-decoration:none; border-radius:4px; font-weight:bold; font-size:16px; display:inline-block;\">"
					+ actionText + "</a></div>";
		}

		return "<!DOCTYPE html>" + "<html lang=\"pt-BR\">" + "<head><meta charset=\"UTF-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "<title>" + title
				+ "</title>" + "</head>"
				+ "<body style=\"font-family:sans-serif; background-color:#f4f4f4; padding:20px; margin:0;\">"

				// Container principal
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px; margin:0 auto; background-color:white; border:1px solid #ccc;\">"
				+ "<tr><td style=\"padding:20px;\">"

				// Logo
				+ "<div style=\"text-align:left; margin-bottom:20px;\">"
				+ "<img src='https://img.freepik.com/vetores-premium/vetor-de-carro-icone-de-detalhamento-de-silhueta-de-carro_676627-184.jpg' alt='AutoStock' style='height:80px;'>"
				+ "</div>"

				// Título
				+ "<h2 style=\"color:#005aa7; margin-top:0;\">" + title + "</h2>"

				// Saudação
				+ "<p style=\"font-size:15px; color:#333; margin: 10px 0;\">" + greeting + "</p>"

				// Mensagem
				+ "<p style=\"font-size:15px; color:#333; margin: 10px 0;\">" + message + "</p>"

				// Botão de ação
				+ buttonHtml

				// Rodapé informativo
				+ "<hr style=\"border:none; border-top:1px solid #eee; margin:30px 0;\">"
				+ "<p style=\"font-size:12px; color:#6e7370; margin-top:10px; line-height:1.5;\">" + footer + "</p>"

				+ "</td></tr>"

				// Rodapé com termos e política
				+ "<tr><td style=\"font-size:11px; color:#999; text-align:center; padding:15px; background-color:#f9f9f9;\">"
				+ "Você está recebendo este e-mail porque criou uma conta na plataforma AutoStock.<br>"
				+ "Leia nossos <a href=\"http://localhost:8585/terms\" style=\"color:#999; text-decoration:underline;\">Termos de Uso</a> e nossa <a href=\"http://localhost:8585/terms\" style=\"color:#999; text-decoration:underline;\">Política de Privacidade</a>."
				+ "</td></tr>"

				+ "</table>" + "</body></html>";

	}

	@Async
	public void sendVerificationEmail(User user, String url) {
		try {
			String subject = "Verificação de e-mail - AutoStock";
			String body = buildEmailTemplate("Verificação de e-mail", "Olá, " + user.getRazaoSocial() + ",",
					"Agradecemos por ter se registrado na plataforma <strong>AutoStock</strong>! 🎉<br><br>"
							+ "Antes de começar a utilizar todos os recursos disponíveis, precisamos confirmar que este endereço de e-mail é realmente seu.<br><br>"
							+ "Para isso, clique no botão abaixo e conclua a verificação da sua conta. Esse processo é rápido e garante a segurança da sua experiência na nossa plataforma.",
					"Verificar e-mail", url,
					"Se você não realizou esse cadastro, ignore esta mensagem. Este link expira em 24 horas.<br><br>"
							+ "Em caso de dúvidas, entre em contato com nossa equipe pelo suporte@autostock.com.<br><br>"
							+ "Nunca compartilhe seu código ou dados de acesso com outras pessoas.");

			emailService.send(subject, body, user.getEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de verificação para {}: {}", user.getEmail(), e.getMessage(), e);
		}
	}

	@Async
	public void sendPasswordResetVerificationEmail(User user, String url) {
		try {
			String subject = "Redefinição de senha - AutoStock";

			String body = buildEmailTemplate("Redefinição de senha", "Olá, " + user.getRazaoSocial() + ",",
					"Recebemos uma solicitação para redefinir a senha da sua conta na plataforma <strong>AutoStock</strong>.<br><br>"
							+ "Se você foi o responsável por essa solicitação, clique no botão abaixo para escolher uma nova senha com segurança.<br><br>"
							+ "Se você não solicitou a redefinição, ignore este e-mail. Sua senha permanecerá a mesma.",
					"Redefinir senha", url,
					"Este link é válido por até 24 horas. Após esse prazo, será necessário realizar uma nova solicitação.<br><br>"
							+ "Se tiver dúvidas ou precisar de ajuda, entre em contato com nossa equipe pelo e-mail suporte@autostock.com.<br><br>"
							+ "<strong>Nunca compartilhe sua senha ou dados de acesso com terceiros.</strong>");
			emailService.send(subject, body, user.getEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de redefinição de senha para {}: {}", user.getEmail(), e.getMessage(), e);
		}
	}

	@Async
	public void sendAccountLockedEmail(User user) {
		try {
			String subject = "Conta bloqueada - AutoStock";

	        String body = buildEmailTemplate(
	            "Conta temporariamente bloqueada",
	            "Olá, " + user.getRazaoSocial() + ",",
	            "Detectamos múltiplas tentativas de login sem sucesso em sua conta na plataforma <strong>AutoStock</strong>.<br><br>"
	            + "Por motivos de segurança, sua conta foi <strong>temporariamente bloqueada</strong>.<br><br>"
	            + "Você poderá tentar novamente após <strong>24 horas</strong>.<br><br>"
	            + "Se você não reconhece essas tentativas, recomendamos que redefina sua senha assim que possível.",
	            null, 
	            null,
	            "Este é um procedimento automático de segurança. Caso tenha dúvidas, entre em contato com nossa equipe pelo e-mail suporte@autostock.com.<br><br>"
	            + "<strong>Nunca compartilhe suas credenciais com terceiros.</strong>"
	        );

			emailService.send(subject, body, user.getEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de conta bloqueada para {}: {}", user.getEmail(), e.getMessage(), e);
		}
	}

	@Async
	public void sendAccountUnlockedEmail(User user) {
		try {
			String subject = "Sua conta foi desbloqueada - AutoStock";

	        String loginUrl = "http://localhost:8585/login";

	        String body = buildEmailTemplate(
	            "Conta desbloqueada com sucesso",
	            "Olá, " + user.getRazaoSocial() + ",",
	            "Sua conta foi <strong>desbloqueada automaticamente</strong> após o tempo de bloqueio expirar.<br><br>"
	            + "Agora você pode tentar fazer login novamente na plataforma <strong>AutoStock</strong>.",
	            "Acessar conta",
	            loginUrl,
	            "Se você não tentou acessar sua conta recentemente, recomendamos alterar sua senha por precaução.<br><br>"
	            + "Em caso de dúvidas, entre em contato com nossa equipe pelo e-mail suporte@autostock.com."
	        );

			emailService.send(subject, body, user.getEmail());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de conta desbloqueada para {}: {}", user.getEmail(), e.getMessage(), e);
		}
	}

	@Async
	public void sendVerificationCodeEmail(String email, String code) {
	    try {
	        String subject = "Código de verificação - AutoStock";

	        String body = "<!DOCTYPE html>"
                + "<html lang=\"pt-BR\">"
                + "<head><meta charset=\"UTF-8\"></head>"
                + "<body style=\"font-family:Arial, sans-serif; background-color:#f4f4f4; padding:20px; margin:0;\">"
                + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px; margin:0 auto; background-color:#ffffff; border:1px solid #ddd;\">"

             	// Logo
				+ "<div style=\"text-align:left; margin-bottom:20px;\">"
				+ "<img src='https://img.freepik.com/vetores-premium/vetor-de-carro-icone-de-detalhamento-de-silhueta-de-carro_676627-184.jpg' alt='AutoStock' style='height:80px;'>"
				+ "</div>"
				
                // Corpo do e-mail
                + "<tr><td style=\"padding:0 20px 20px 20px;\">"
                + "<h2 style=\"color:#333;\">Olá,</h2>"
                + "<p style=\"font-size:15px; color:#333;\">"
                + "Você solicitou um código de verificação para continuar um processo de autenticação na plataforma <strong>AutoStock</strong>."
                + "</p>"
                + "<p style=\"font-size:15px; color:#333;\">"
                + "Use o código abaixo para confirmar sua identidade:"
                + "</p>"

                // Código em destaque
                + "<div style=\"text-align:center; font-size:28px; font-weight:bold; color:#005aa7; margin:30px 0;\">"
                + code
                + "</div>"

                // Avisos
                + "<p style=\"font-size:14px; color:#333;\">"
                + "Este código é válido por <strong>10 minutos</strong>."
                + "</p>"
                + "<p style=\"font-size:14px; color:#333;\">"
                + "Se você não solicitou este código, apenas ignore este e-mail."
                + "</p>"

                + "<p style=\"font-size:14px; color:#333;\">"
                + "Atenciosamente,<br>Equipe AutoStock"
                + "</p>"
                + "</td></tr>"

                // Rodapé cinza
                + "<tr><td style=\"padding:10px; font-size:11px; color:#888; text-align:center; background-color:#f9f9f9;\">"
                + "Você está recebendo este e-mail porque está cadastrado na plataforma AutoStock. "
                + "Consulte nossos <a href=\"http://localhost:8585/terms\" style=\"color:#888; text-decoration:underline;\">Termos de Uso</a> e "
                + "<a href=\"http://localhost:8585/terms\" style=\"color:#888; text-decoration:underline;\">Política de Privacidade</a>."
                + "</td></tr>"

                + "</table></body></html>";

	        emailService.send(subject, body, email);
	    } catch (Exception e) {
	        log.error("Erro ao enviar código de verificação para {}: {}", email, e.getMessage(), e);
	    }
	}
	
	@Async
	public void sendContractAndWarrantyToCustomer(
	        String customerEmail,
	        String customerName,
	        byte[] contractPdf,
	        byte[] warrantyPdf,
	        String vehicleInfo) {
	    try {
	        String subject = "Contrato e Termo de Garantia - AutoStock";

	        String body = buildEmailTemplate(
	            "Contrato e Termo de Garantia",
	            "Olá, " + customerName + ",",
	            "Segue em anexo o seu <strong>Contrato de Compra e Venda</strong> e o <strong>Termo de Garantia</strong> "
	            + "referentes ao veículo <strong>" + vehicleInfo + "</strong>.<br><br>"
	            + "Por favor, revise ambos os documentos e entre em contato com a agência em caso de dúvidas.",
	            null,
	            null,
	            "Estes documentos foram emitidos automaticamente pela plataforma AutoStock.<br>"
	            + "Em caso de dúvidas, entre em contato com a agência responsável pela venda."
	        );

	        emailService.sendWithAttachments(
	            subject,
	            body,
	            customerEmail,
	            new byte[][]{ contractPdf, warrantyPdf },
	            new String[]{
	                "Contrato_" + vehicleInfo.replace(" ", "_") + ".pdf",
	                "Garantia_" + vehicleInfo.replace(" ", "_") + ".pdf"
	            }
	        );

	        log.info("📨 Contrato e garantia enviados para o cliente: {}", customerEmail);
	    } catch (Exception e) {
	        log.error("Erro ao enviar contrato e garantia para o cliente {}: {}", customerEmail, e.getMessage(), e);
	    }
	}

	@Async
	public void sendContractAndWarrantyToAgency(
	        String agencyEmail,
	        String agencyName,
	        byte[] contractPdf,
	        byte[] warrantyPdf,
	        String vehicleInfo) {
	    try {
	        String subject = "Cópia do Contrato e Termo de Garantia - " + vehicleInfo;

	        String body = buildEmailTemplate(
	            "Cópia do Contrato e Termo de Garantia",
	            "Olá, " + agencyName + ",",
	            "Segue cópia do <strong>Contrato de Venda</strong> e do <strong>Termo de Garantia</strong> "
	            + "referentes ao veículo <strong>" + vehicleInfo + "</strong>.<br><br>"
	            + "Os documentos também foram enviados ao cliente.",
	            null,
	            null,
	            "Guarde estes arquivos para seus registros internos.<br>"
	            + "Mensagem enviada automaticamente pelo sistema AutoStock."
	        );

	        emailService.sendWithAttachments(
	            subject,
	            body,
	            agencyEmail,
	            new byte[][]{ contractPdf, warrantyPdf },
	            new String[]{
	                "Contrato_" + vehicleInfo.replace(" ", "_") + ".pdf",
	                "Garantia_" + vehicleInfo.replace(" ", "_") + ".pdf"
	            }
	        );

	        log.info("📨 Contrato e garantia enviados para a agência: {}", agencyEmail);
	    } catch (Exception e) {
	        log.error("Erro ao enviar contrato e garantia para a agência {}: {}", agencyEmail, e.getMessage(), e);
	    }
	}
	
	@Async
	public void sendEmployeeInviteEmail(String employeeEmail, User agency, String inviteUrl) {

	    String agencyName = getAgencyDisplayName(agency);

	    String subject = "Você foi contratado pela agência " + agencyName + "!";

	    String body = buildEmailTemplate(
	            "Você foi contratado pela agência " + agencyName + "!",
	            "Bem-vindo(a) à equipe! 🎉",
	            "A agência <strong>" + agencyName + "</strong> está convidando você para fazer parte da equipe na plataforma <strong>AutoStock</strong>.<br><br>"
	            + "Para concluir seu cadastro, clique no botão abaixo e preencha seus dados.",
	            "Preencher cadastro",
	            inviteUrl,
	            "Se você não esperava este convite, apenas ignore este e-mail.<br>"
	            + "O link é válido por 24 horas."
	    );

	    emailService.send(subject, body, employeeEmail);
	}

	private String getAgencyDisplayName(User agency) {
	    if (agency.getNomeFantasia() != null && !agency.getNomeFantasia().isBlank()) {
	        return agency.getNomeFantasia();
	    }
	    return agency.getRazaoSocial();
	}

}