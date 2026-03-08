package br.com.AutoStock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VehicleYearValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface VehicleYear {
    String message() default "Ano inválido. Deve estar entre 1886 e o limite configurado.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
