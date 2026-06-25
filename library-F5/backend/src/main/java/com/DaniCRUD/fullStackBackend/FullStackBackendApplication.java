package com.DaniCRUD.fullStackBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FullStackBackendApplication {

	public static void main(String[] args) {
		String directory = "./";
		if (!new java.io.File(".env").exists()) {
			if (new java.io.File("../.env").exists()) {
				directory = "../";
			} else if (new java.io.File("../../.env").exists()) {
				directory = "../../";
			}
		}

		Dotenv dotenv = Dotenv.configure()
				.directory(directory)
				.ignoreIfMissing()
				.load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication.run(FullStackBackendApplication.class, args);
	}

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}