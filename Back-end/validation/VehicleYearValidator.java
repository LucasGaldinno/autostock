package br.com.AutoStock.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VehicleYearValidator implements ConstraintValidator<VehicleYear, Integer> {

    private static final int MIN_YEAR = 1886;
    private static final int MAX_YEAR = 2026;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return true; // deixa @NotNull cuidar
        return value >= MIN_YEAR && value <= MAX_YEAR;
    }
}
