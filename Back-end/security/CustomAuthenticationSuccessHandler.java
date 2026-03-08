package br.com.AutoStock.security;

import br.com.AutoStock.model.Employee;
import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.IAuthenticationService;
import br.com.AutoStock.service.VerificationCodeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IAuthenticationService authService;
    private final VerificationCodeService verificationCodeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 1) ADMIN continua separado
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            response.sendRedirect("/admin/dashboard");
            return;
        }

        Object principal = authentication.getPrincipal();

        // 2) LOGIN DE AGÊNCIA (User)
        if (principal instanceof CustomUserDetails customUserDetails) {

            User user = customUserDetails.getUser();
            authService.handleSuccessfulLogin(user);

            if (user.isFirstLoginCompleted()) {
                // fluxo 2FA
                verificationCodeService.generateAndSendCode(user.getEmail());

                request.getSession().setAttribute("2fa_pending", true);
                request.getSession().setAttribute("verificado", false);
                request.getSession().setAttribute("authEmail", user.getEmail());

                response.sendRedirect("/verify-code");
                return;
            }

            user.setFirstLoginCompleted(true);
            authService.salvar(user);

            response.sendRedirect("/home");
            return;
        }

        if (principal instanceof EmployeeUserDetails employeeUserDetails) {

            Employee employee = employeeUserDetails.getEmployee();

            // salva ID do funcionário na sessão
            request.getSession().setAttribute("employeeId", employee.getId());

            // FUNCIONÁRIO SEMPRE PASSA POR 2FA
            verificationCodeService.generateAndSendCode(employee.getEmail());

            request.getSession().setAttribute("2fa_pending", true);
            request.getSession().setAttribute("verificado", false);
            request.getSession().setAttribute("authEmail", employee.getEmail());
            request.getSession().setAttribute("loginType", "employee");

            response.sendRedirect("/verify-code");
            return;
        }

        // fallback
        response.sendRedirect("/home");
    }
}
