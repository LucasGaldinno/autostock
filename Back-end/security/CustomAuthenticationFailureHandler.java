package br.com.AutoStock.security;

import br.com.AutoStock.model.User;
import br.com.AutoStock.repository.IAuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final IAuthenticationService authService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

    	String loginParam = request.getParameter("login");

        if (isCausedBy(exception, LockedException.class)) {
            response.sendRedirect("/login?bloqueado=true");
            return;
        }

        Optional<User> optionalUser = authService.findByEmailOrCnpj(loginParam);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.isAccountLocked()) {
                user = authService.handleFailedLogin(user);

                if (user.getFailedAttempts() >= 5) {
                    response.sendRedirect("/login?locked=true");
                    return;
                }
            }

            if (exception instanceof BadCredentialsException || isCausedBy(exception, BadCredentialsException.class)) {
                int tentativasRestantes = 5 - user.getFailedAttempts();
                response.sendRedirect("/login?error=true&tentativas=" + tentativasRestantes);
                return;
            }
        }

        response.sendRedirect("/login?error=true");
    }

    private boolean isCausedBy(Throwable ex, Class<? extends Throwable> clazz) {
        while (ex != null) {
            if (clazz.isInstance(ex)) return true;
            ex = ex.getCause();
        }
        return false;
    }
}
