package com.example.aiapp.aiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.aiapp.aiapi"}) // Add this
public class AiapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(AiapiApplication.class, args);
	}
}