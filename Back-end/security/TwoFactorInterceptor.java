package br.com.AutoStock.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TwoFactorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Boolean is2faPending = (Boolean) request.getSession().getAttribute("2fa_pending");

            if (Boolean.TRUE.equals(is2faPending)
                    && !request.getRequestURI().startsWith("/verify-code")) {
                response.sendRedirect("/verify-code");
                return false;
            }
        }

        return true;
    }
}
