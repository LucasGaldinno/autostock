package br.com.AutoStock.validation;

import br.com.AutoStock.model.Vehicle;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearRangeValidator implements ConstraintValidator<YearRange, Vehicle> {

    @Override
    public boolean isValid(Vehicle v, ConstraintValidatorContext context) {
        if (v == null) return true;

        Integer fab = v.getManufactureYear(); // você precisa ter este campo no Vehicle
        Integer mod = v.getModelYear();       // idem

        if (fab == null || mod == null) return true;

        if (mod < fab || mod > fab + 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Ano do modelo deve ser igual ao de fabricação ou no máximo +1."
            ).addPropertyNode("modelYear").addConstraintViolation();
            return false;
        }
        return true;
    }
}
