package br.com.AutoStock.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RenavamValidator implements ConstraintValidator<Renavam, String> {

    private boolean checkDigit;

    @Override
    public void initialize(Renavam constraintAnnotation) {
        this.checkDigit = constraintAnnotation.checkDigit();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 11) return false;

        if (!checkDigit) return true;

        return isValidRenavamDV(digits);
    }

    private boolean isValidRenavamDV(String renavam) {
        // Pega os 10 primeiros dígitos
        String base = renavam.substring(0, 10);
        int expectedDV = Character.getNumericValue(renavam.charAt(10));

        // Inverte a string (regra oficial)
        String reversed = new StringBuilder(base).reverse().toString();

        int[] pesos = {2, 3, 4, 5, 6, 7, 8, 9}; // sequência de pesos
        int soma = 0;

        for (int i = 0; i < reversed.length(); i++) {
            int digito = Character.getNumericValue(reversed.charAt(i));
            soma += digito * pesos[i % pesos.length];
        }

        int resto = soma % 11;
        int dvCalculado = 11 - resto;
        if (dvCalculado >= 10) dvCalculado = 0;

        return dvCalculado == expectedDV;
    }
}
