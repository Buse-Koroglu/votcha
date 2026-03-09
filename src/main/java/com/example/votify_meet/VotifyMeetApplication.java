package com.example.votify_meet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VotifyMeetApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotifyMeetApplication.class, args);
	}

}
