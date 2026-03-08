package br.com.AutoStock.config;

import org.springframework.stereotype.Component;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Component
@Getter
public class AdminProperties {

    private final String email;
    private final String password;
    private final String role;

    public AdminProperties() {
        Dotenv dotenv = Dotenv.load();

        this.email = dotenv.get("ADMIN_EMAIL");
        this.password = dotenv.get("ADMIN_PASSWORD");
        this.role = dotenv.get("ADMIN_ROLE", "ROLE_ADMIN"); // default
    }
}
