package com.example.Perfect_Gift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PerfectGiftApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfectGiftApplication.class, args);
	}

}
