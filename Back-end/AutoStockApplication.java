package br.com.AutoStock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
@EnableScheduling
public class AutoStockApplication {

	public static void main(String[] args) {
		io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().load();

	    System.setProperty("DB_USER", dotenv.get("DB_USER"));
	    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
	    System.setProperty("MAIL_USER", dotenv.get("MAIL_USER"));
	    System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
	    
		SpringApplication.run(AutoStockApplication.class, args);
	}

}
