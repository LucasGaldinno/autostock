package br.com.AutoStock.validation;

import br.com.AutoStock.model.Vehicle;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.NumberFormat;
import java.time.Year;
import java.util.Locale;

public class MileageValidator implements ConstraintValidator<ValidMileage, Vehicle> {

    private static final long HARD_CAP = 10_000_000L; // 10 milhões km como limite global

    @Override
    public boolean isValid(Vehicle vehicle, ConstraintValidatorContext context) {
        if (vehicle == null || vehicle.getMileage() == null || vehicle.getManufactureYear() == null) {
            return true; // deixa o @NotNull cuidar disso
        }

        int currentYear = Year.now().getValue();
        int age = currentYear - vehicle.getManufactureYear();
        if (age <= 0) {
            age = 1;
        }

        long maxExpectedMileage = age * 200_000L;
        long allowedMileage = Math.min(maxExpectedMileage, HARD_CAP);

        if (vehicle.getMileage() > allowedMileage) {
            NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
            String allowedMileageFormatted = nf.format(allowedMileage);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Quilometragem muito alta para um veículo de " + age +
                " anos (máx. permitido: " + allowedMileageFormatted + " km)"
            ).addPropertyNode("mileage").addConstraintViolation();
            return false;
        }

        return true;
    }
}
