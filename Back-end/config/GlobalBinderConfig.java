package br.com.AutoStock.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@ControllerAdvice
public class GlobalBinderConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        // Conversor padrão BR para Double (ex: 12.000,50 -> 12000.50)
        binder.registerCustomEditor(Double.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }

                String normalized = text.replace(".", "").replace(",", ".");
                try {
                    setValue(Double.parseDouble(normalized));
                } catch (NumberFormatException e) {
                    setValue(null);
                }
            }
        });

        // Conversor para Long (quilometragem) SEM casas decimais
        binder.registerCustomEditor(Long.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }

                // Remove pontos e vírgulas, ignora decimais
                String normalized = text.replace(".", "").replace(",", "");

                try {
                    setValue(Long.parseLong(normalized));
                } catch (NumberFormatException e) {
                    setValue(null);
                }
            }
        });
    }
}
