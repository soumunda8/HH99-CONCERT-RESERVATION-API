package io.hhplus.concert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConcertReservationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcertReservationApiApplication.class, args);
	}

}
