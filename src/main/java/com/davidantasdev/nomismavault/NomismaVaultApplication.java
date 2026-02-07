package com.davidantasdev.nomismavault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NomismaVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(NomismaVaultApplication.class, args);
	}

}
