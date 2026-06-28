package com.example.aiapp.aiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.aiapp.aiapi"}) // Add this
public class AiapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(AiapiApplication.class, args);

		System.out.println("MAIL_USERNAME from env: " + System.getenv("MAIL_USERNAME"));
		System.out.println("MAIL_PASSWORD from env: " + (System.getenv("MAIL_PASSWORD") != null ? "SET" : "NOT SET"));
		System.out.println("FRONTEND_URL_PROD from env: " + System.getenv("FRONTEND_URL_PROD"));
	}
}