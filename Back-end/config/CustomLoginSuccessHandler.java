package br.com.AutoStock.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.UserRepository;
import br.com.AutoStock.service.VerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final VerificationCodeService codeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow();

        if (!user.isFirstLoginCompleted()) {
            user.setFirstLoginCompleted(true);
            userRepo.save(user);
            response.sendRedirect("/home");
        } else {
            codeService.generateAndSendCode(email);
            request.getSession().setAttribute("authEmail", email);
            request.getSession().setAttribute("2fa_pending", true);
            response.sendRedirect("/verify-code");
        }
    }
}
