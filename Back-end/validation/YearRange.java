package br.com.AutoStock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = YearRangeValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface YearRange {
    String message() default "Ano do modelo deve ser igual ao de fabricação ou no máximo +1.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
