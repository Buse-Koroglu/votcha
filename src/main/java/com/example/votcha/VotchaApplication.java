package com.example.votcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VotchaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotchaApplication.class, args);
	}

}
