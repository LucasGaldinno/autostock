package br.com.AutoStock.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;
import org.xbill.DNS.SimpleResolver;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class EmailDomainValidatorImpl implements ConstraintValidator<EmailDomainValid, String> {

    private static final List<String> DOMINIOS_ACEITOS = Arrays.asList(
        ".com", ".com.br", ".org", ".gov.br", ".edu.br", ".net"
    );

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return true;

        // Regex mais rígida: exige nome, @, domínio e TLD (ex: .com)
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Formato de e-mail inválido. Ex: exemplo@dominio.com")
                   .addConstraintViolation();
            return false;
        }

        try {
            String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

            boolean dominioValido = DOMINIOS_ACEITOS.stream().anyMatch(domain::endsWith);
            if (!dominioValido) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Domínio de e-mail inválido ou não permitido.")
                       .addConstraintViolation();
                return false;
            }

            Lookup lookup = new Lookup(domain, Type.MX);
            SimpleResolver resolver = new SimpleResolver();
            resolver.setTimeout(Duration.ofSeconds(2));
            lookup.setResolver(resolver);

            Record[] records = lookup.run();
            if (records == null || Arrays.stream(records).noneMatch(r -> r instanceof MXRecord)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Domínio de e-mail inválido ou sem servidor de e-mail (MX)")
                       .addConstraintViolation();
                return false;
            }

        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Erro ao validar domínio de e-mail.")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
