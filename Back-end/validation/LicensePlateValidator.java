package br.com.AutoStock.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class LicensePlateValidator implements ConstraintValidator<LicensePlate, String> {

    // Padrão antigo: AAA-1234 (aceita com ou sem hífen)
    private static final Pattern OLD_PATTERN =
            Pattern.compile("^[A-Z]{3}-?[0-9]{4}$");

    // Padrão Mercosul: BRA2E19
    private static final Pattern MERCOSUL_PATTERN =
            Pattern.compile("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;

        String plate = value.trim().toUpperCase();
        return OLD_PATTERN.matcher(plate).matches()
                || MERCOSUL_PATTERN.matcher(plate).matches();
    }
}
