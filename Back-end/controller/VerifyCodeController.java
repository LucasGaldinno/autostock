package br.com.AutoStock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.AutoStock.service.VerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VerifyCodeController {

    private final VerificationCodeService codeService;

    @GetMapping("/verify-code")
    public String showCodeForm(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "resent", required = false) String resent,
                               HttpServletRequest request) {

        if (error != null) {
            request.setAttribute("codigo_invalido", true);
        }

        if (resent != null) {
            request.setAttribute("codigo_resent", true);
        }

        return "verify-code";
    }

    @PostMapping("/verify-code")
    public String verifyCode(HttpServletRequest request, @RequestParam("code") String code) {
        String email = (String) request.getSession().getAttribute("authEmail");

        if (email != null && codeService.validateCode(email, code)) {
        	request.getSession().setAttribute("verificado", true);
            request.getSession().removeAttribute("2fa_pending");
            request.getSession().removeAttribute("authEmail");

            return "redirect:/home";
        }

        return "redirect:/verify-code?error";
    }

    @GetMapping("/verify-code/resend")
    public String resendCode(HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("authEmail");

        if (email != null) {
            System.out.println("Reenviando código para: " + email);
            codeService.generateAndSendCode(email);
        } else {
            System.out.println("Nenhum e-mail encontrado na sessão!");
        }

        return "redirect:/verify-code?resent=true";
    }
}
