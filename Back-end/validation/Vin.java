package br.com.AutoStock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = VinValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Vin {
    // VIN: 17 caracteres, sem I, O, Q; opcionalmente valida dígito verificador (pos. 9)
    String message() default "Chassi (VIN) inválido. Deve ter 17 caracteres alfanuméricos (sem I, O ou Q) e passar na validação.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean checkDigit() default true; // valida o DV no 9º caractere (ISO 3779)
}
