package strava.client.proxies;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import strava.client.data.Challenge;
import strava.client.data.Credentials;
import strava.client.data.TrainingSession;
import strava.client.data.User;

@Service
public class RestTemplateServiceProxy implements IStravaServiceProxy {
	private final RestTemplate restTemplate;
	
	@Value("${api.base.url}")
	private String apiBaseUrl;
	
	public RestTemplateServiceProxy(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	@Override
    public User user(User user) {
        String url = apiBaseUrl + "/users/user";
        
        try {
            return restTemplate.postForObject(url, user, User.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new RuntimeException("User registration failed: Invalid input.");
                case 500 -> throw new RuntimeException("Internal server error during user registration.");
                default -> throw new RuntimeException("User registration failed: " + e.getStatusText());
            }
        }
    }

	@Override
    public Map<String, String> login(Credentials credentials) {
        String url = apiBaseUrl + "/users/login";
        
        try {
            return restTemplate.postForObject(url, credentials, Map.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new RuntimeException("Login failed: Invalid credentials.");
                case 404 -> throw new RuntimeException("Login failed: User not found.");
                default -> throw new RuntimeException("Login failed: " + e.getStatusText());
            }
        }
    }

	@Override
    public Map<String, String> logout(String userId, String token) {
        String url = apiBaseUrl + "/users/" + userId + "/logout";
        
        try {
            restTemplate.postForObject(url, token, Void.class);
            return Map.of("validation", "Logout successful");
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new RuntimeException("Logout failed: Invalid token.");
                case 404 -> throw new RuntimeException("Logout failed: User not found.");
                default -> throw new RuntimeException("Logout failed: " + e.getStatusText());
            }
        }
    }

	@Override
	public TrainingSession session(String userId, String token, String title, String sport, Double distance,
			Date startDate, Integer duration) {
		
		return null;
	}

	@Override
	public List<TrainingSession> sessions(String userId, String token, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Challenge challenge(String userId, String token, String challengeName, Date startDate, Date endDate,
			Double targetTime, Double targetDistance, String sport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Challenge> challenges(Date startDate, Date endDate, String sport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Challenge> challengeParticipant(String challengeId, String userId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Float> challengeStatus(String userId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
