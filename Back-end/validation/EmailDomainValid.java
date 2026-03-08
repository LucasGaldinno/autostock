package br.com.AutoStock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailDomainValidatorImpl.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailDomainValid {
    String message() default "Domínio de e-mail inválido ou sem servidor de e-mail (MX)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
