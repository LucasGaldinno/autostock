package br.com.AutoStock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MileageValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMileage {
    String message() default "Quilometragem incompatível com o ano de fabricação";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
