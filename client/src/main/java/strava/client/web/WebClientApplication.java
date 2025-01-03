package strava.client.web;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


/**
 * WebClientApplication class is the main class for the web client application.
 */

// Anotar el motivo de la ubicación.
@SpringBootApplication(scanBasePackages = { "strava" })
public class WebClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebClientApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}