package strava.client.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import strava.client.proxies.HttpServiceProxy;
import strava.client.data.Challenge;
import strava.client.data.Credentials;
import strava.client.data.User;


@SpringBootApplication
public class ClientConsole {
	
	    // Service proxy for interacting with the AuctionsService using HTTP-based implementation
		private final HttpServiceProxy serviceProxy = new HttpServiceProxy();	
		// Token to be used during the session
		private String token;
		// Default email and password for login
		private String defaultEmail = "user1@example.com";
		private String defaultPassword = "password123";
		private String defaultExternalProv = "Facebook";
		
		private static final Logger logger = LoggerFactory.getLogger(ClientConsole.class);


	public static void main(String[] args) {
		SpringApplication.run(ClientConsole.class, args);
		
		ClientConsole client = new ClientConsole();
		
		if (!client.performLogin()) {
			logger.info("Exiting application due to failure in one of the steps.");
		}
	}
	
	public boolean performLogin() {
		try {
			Credentials credentials = new Credentials(defaultEmail, defaultPassword, defaultExternalProv);

			token = serviceProxy.login(credentials).get("token");
			logger.info("Login successful. Token: {}", token);

			return true;
		} catch (RuntimeException e) {
			logger.error("Login failed: {}", e.getMessage());
			
			return false;
		}
	}

}
