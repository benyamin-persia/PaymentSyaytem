package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BankSystemApplication {

    public static void main(String[] args) {                                                                  // Concept: Method | Why: encapsulates main behavior for this component boundary.
        SpringApplication.run(BankSystemApplication.class, args);
    }
}
