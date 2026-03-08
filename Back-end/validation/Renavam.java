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
@Constraint(validatedBy = RenavamValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Renavam {
    String message() default "RENAVAM inválido. Deve conter 11 dígitos e passar na validação.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean checkDigit() default true;
}
