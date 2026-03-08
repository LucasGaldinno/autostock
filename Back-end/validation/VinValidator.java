package br.com.AutoStock.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VinValidator implements ConstraintValidator<Vin, String> {

    private boolean checkDigit;

    // VIN: sem I, O, Q
    private static final String VIN_CHARS = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";

    // Pesos padrão ISO 3779
    private static final int[] WEIGHTS = {
        8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2
    };

    // Tabela de transliteração (ISO 3779)
    private static final int[] TRANSLITERATION = new int[128];
    static {
        // Mapeia letras para valores numéricos conforme ISO 3779
        String letters = "A B C D E F G H J K L M N P R S T U V W X Y Z".replace(" ", "");
        int[] values   = {1,2,3,4,5,6,7,8,1,2,3,4,5,7,9,2,3,4,5,6,7,8,9};
        for (int i = 0; i < letters.length(); i++) {
            TRANSLITERATION[letters.charAt(i)] = values[i];
        }
        // Números: 0–9 → 0–9
        for (int i = 0; i <= 9; i++) {
            TRANSLITERATION['0' + i] = i;
        }
    }

    @Override
    public void initialize(Vin constraintAnnotation) {
        this.checkDigit = constraintAnnotation.checkDigit();
    }

    @Override
    public boolean isValid(String vin, ConstraintValidatorContext context) {
        if (vin == null) return false;
        vin = vin.trim().toUpperCase();
        if (vin.length() != 17) return false;

        // Verifica caracteres válidos
        for (char c : vin.toCharArray()) {
            if (c >= TRANSLITERATION.length || VIN_CHARS.indexOf(c) == -1) {
                return false;
            }
        }

        // Se não precisa verificar o dígito verificador, apenas valida formato
        if (!checkDigit) return true;

        // Calcula dígito verificador (posição 9)
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = vin.charAt(i);
            int value = TRANSLITERATION[c];
            sum += value * WEIGHTS[i];
        }

        int remainder = sum % 11;
        char expected = (remainder == 10) ? 'X' : (char) ('0' + remainder);

        return vin.charAt(8) == expected;
    }
}
