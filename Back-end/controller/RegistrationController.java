package br.com.AutoStock.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import br.com.AutoStock.dto.PasswordResetRequest;
import br.com.AutoStock.dto.RegistrationRequest;
import br.com.AutoStock.event.RegistrationCompleteEvent;
import br.com.AutoStock.event.RegistrationCompleteEventListener;
import br.com.AutoStock.exception.CnpjDuplicadoException;
import br.com.AutoStock.exception.CnpjInvalidoException;
import br.com.AutoStock.exception.EmailDuplicadoException;
import br.com.AutoStock.model.User;
import br.com.AutoStock.model.VerificationToken;
import br.com.AutoStock.repository.IPasswordResetTokenService;
import br.com.AutoStock.repository.IUserService;
import br.com.AutoStock.service.PasswordHistoryService;
import br.com.AutoStock.service.VerificationTokenService;
import br.com.AutoStock.utility.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {
	private final IUserService userService;
	private final ApplicationEventPublisher publisher;
	private final VerificationTokenService tokenService;
	private final IPasswordResetTokenService passwordResetTokenService;
	private final RegistrationCompleteEventListener eventListener;
	@Autowired
	private PasswordHistoryService passwordHistoryService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/registration-form")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new RegistrationRequest());
		return "registration";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") RegistrationRequest registration, BindingResult result,
			HttpServletRequest request, Model model) {

		if (result.hasErrors()) {
			adicionarAtributosDeErro(result, model);
			// ✅ identifica qual etapa tem erro e adiciona atributo
			model.addAttribute("errorStep", getStepWithError(result));
			return "registration";
		}

		try {
			User user = userService.registerUser(registration);
			passwordHistoryService.savePassword(user, user.getPassword());
			publisher.publishEvent(new RegistrationCompleteEvent(user, UrlUtil.getApplicationUrl(request)));
		} catch (EmailDuplicadoException e) {
			result.rejectValue("email", "duplicado", e.getMessage());
			adicionarAtributosDeErro(result, model);
			model.addAttribute("errorStep", "3"); // email → etapa 3
			return "registration";
		} catch (CnpjDuplicadoException e) {
			result.rejectValue("cnpj", "duplicado", e.getMessage());
			adicionarAtributosDeErro(result, model);
			model.addAttribute("errorStep", "1");
			return "registration";
		} catch (CnpjInvalidoException e) {
			result.rejectValue("cnpj", "invalido", e.getMessage());
			adicionarAtributosDeErro(result, model);
			model.addAttribute("errorStep", "1");
			return "registration";
		}

		return "redirect:/registration/registration-form?success";
	}

	private void adicionarAtributosDeErro(BindingResult result, Model model) {
		model.addAttribute("user", result.getTarget());
		model.addAttribute("org.springframework.validation.BindingResult.user", result);

		if (result.hasFieldErrors("email")) {
			var emailErro = result.getFieldError("email").getCode();
			switch (emailErro) {
			case "Email" -> model.addAttribute("email_format_invalido", true);
			case "duplicado" -> model.addAttribute("email_duplicado", true);
			case "dominio_invalido" -> model.addAttribute("dominio_invalido", true);
			}
		}

		if (result.hasFieldErrors("password")) {
			model.addAttribute("senha_invalida", true);
		}

		if (result.hasFieldErrors("cnpj")) {
			var cnpjErro = result.getFieldError("cnpj").getCode();
			switch (cnpjErro) {
			case "duplicado" -> model.addAttribute("cnpj_duplicado", true);
			case "invalido" -> model.addAttribute("cnpj_invalido", true);
			}
		}

		if (result.hasFieldErrors("termsAccepted")) {
			model.addAttribute("termos_nao_aceitos", true);
		}
	}

	private String getStepWithError(BindingResult result) {
		if (result.hasFieldErrors("cnpj") || result.hasFieldErrors("razaoSocial")
				|| result.hasFieldErrors("nomeFantasia") || result.hasFieldErrors("inscricaoEstadual")) {
			return "1";
		}
		if (result.hasFieldErrors("cep") || result.hasFieldErrors("logradouro") || result.hasFieldErrors("bairro")
				|| result.hasFieldErrors("cidade") || result.hasFieldErrors("uf")) {
			return "2";
		}
		if (result.hasFieldErrors("email") || result.hasFieldErrors("telefone") || result.hasFieldErrors("password")
				|| result.hasFieldErrors("termsAccepted")) {
			return "3";
		}
		return "1"; // fallback padrão
	}

	@GetMapping("/verifyEmail")
	public String verifyEmail(@RequestParam("token") String token) {
		Optional<VerificationToken> theToken = tokenService.findByToken(token);
		if (theToken.isPresent() && theToken.get().getUser().isEnabled()) {
			return "redirect:/login?verified";
		}
		String verificationResult = tokenService.validateToken(token);
		switch (verificationResult.toLowerCase()) {
		case "expired":
			return "redirect:/error?expired";
		case "valid":
			return "redirect:/login?valid";
		default:
			return "redirect:/error?invalid";
		}
	}

	@GetMapping("/forgot-password-request")
	public String forgotPasswordForm() {
		return "forgot-password-form";
	}

	@PostMapping("/forgot-password")
	public String resetPasswordRequest(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		Optional<User> userOptional = userService.findByEmail(email);

		if (userOptional.isEmpty()) {
			return "redirect:/registration/forgot-password-request?not_found";
		}

		User user = userOptional.get();

		if (!user.isEnabled()) {
			return "redirect:/registration/forgot-password-request?not_verified";
		}

		if (user.isAccountLocked()) {
			return "redirect:/registration/forgot-password-request?locked";
		}

		String passwordResetToken = UUID.randomUUID().toString();
		passwordResetTokenService.createPasswordResetTokenForUser(user, passwordResetToken);

		String url = UrlUtil.getApplicationUrl(request) + "/registration/password-reset-form?token="
				+ passwordResetToken;

		eventListener.sendPasswordResetVerificationEmail(user, url);
		return "redirect:/registration/forgot-password-request?success";
	}

	@GetMapping("/password-reset-form")
	public String passwordResetForm(@RequestParam("token") String token, Model model) {
		String tokenValidation = passwordResetTokenService.validatePasswordResetToken(token);

		if ("invalid".equalsIgnoreCase(tokenValidation)) {
			return "redirect:/error?not_found";
		} else if ("expired".equalsIgnoreCase(tokenValidation)) {
			return "redirect:/error?invalid_token";
		}

		PasswordResetRequest resetRequest = new PasswordResetRequest();
		resetRequest.setToken(token);
		model.addAttribute("resetRequest", resetRequest);

		return "password-reset-form";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@Valid @ModelAttribute("resetRequest") PasswordResetRequest resetRequest,
			BindingResult result, Model model) {

		String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(resetRequest.getToken());

		if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
			return "redirect:/error?invalid_token";
		}

		if (result.hasErrors()) {
			return "password-reset-form";
		}

		Optional<User> theUser = passwordResetTokenService.findUserByPasswordResetToken(resetRequest.getToken());
		if (theUser.isPresent()) {
			User user = theUser.get();

			if (passwordHistoryService.isPasswordReused(user, resetRequest.getPassword())) {
				result.rejectValue("password", "repetida", "Você já usou essa senha antes. Escolha outra.");
				return "password-reset-form";
			}

			String encodedPassword = passwordEncoder.encode(resetRequest.getPassword());

			passwordResetTokenService.resetPassword(user, encodedPassword);
			passwordHistoryService.savePassword(user, encodedPassword);

			return "redirect:/login?reset_success";
		}

		return "redirect:/error?not_found";
	}

}