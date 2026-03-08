package br.com.AutoStock.utility;

import java.util.Calendar;
import java.util.Date;

public class TokenExpirationTime {
	// Expiração em minutos
    private static final int PASSWORD_RESET_EXPIRATION_MINUTES = 5;
    private static final int ACCOUNT_VERIFICATION_EXPIRATION_MINUTES = 5;

    // Token para redefinição de senha
    public static Date getPasswordResetExpirationTime() {
        return calculateExpiration(PASSWORD_RESET_EXPIRATION_MINUTES);
    }

    // Token para verificação de conta
    public static Date getAccountVerificationExpirationTime() {
        return calculateExpiration(ACCOUNT_VERIFICATION_EXPIRATION_MINUTES);
    }

    // Método genérico para calcular tempo de expiração
    private static Date calculateExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, expirationMinutes);
        return calendar.getTime();
    }
}