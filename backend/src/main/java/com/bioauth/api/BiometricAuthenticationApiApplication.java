package com.bioauth.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application entry point for Biometric Authentication REST API
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.bioauth"})
public class BiometricAuthenticationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiometricAuthenticationApiApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Biometric Authentication API Started");
        System.out.println("REST API: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
