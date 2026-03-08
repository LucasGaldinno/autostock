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
@Constraint(validatedBy = LicensePlateValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface LicensePlate {
    String message() default "Placa inválida. Use o formato Mercosul (BRA2E19) ou o antigo (AAA-1234).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
