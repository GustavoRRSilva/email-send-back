package com.engeman.technical_test_engeman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechnicalTestEngemanApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechnicalTestEngemanApplication.class, args);
	    // DEBUG: Verificar se as variáveis estão sendo lidas
        System.out.println("DB_URL: " + System.getenv("DB_URL"));
        System.out.println("DB_USER: " + System.getenv("DB_USER"));
        System.out.println("DB_PASS: " + System.getenv("DB_PASS"));
        
        SpringApplication.run(TechnicalTestEngemanApplication.class, args);
	}

}
