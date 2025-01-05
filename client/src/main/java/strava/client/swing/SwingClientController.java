package strava.client.swing;

import java.util.Date;
import java.util.List;
import java.util.Map;

import strava.client.data.Challenge;
import strava.client.data.Credentials;
import strava.client.data.TrainingSession;
import strava.client.data.User;
import strava.client.proxies.HttpServiceProxy;

public class SwingClientController {
	
	private HttpServiceProxy serviceProxy = new HttpServiceProxy();
	// Token to be used during the session
    private String token;
    
    
    public User user(User user) {
		return serviceProxy.user(user);
	}

    public Map<String, String> login(Credentials credentials) {
		try {
			Map<String,String> map = serviceProxy.login(credentials);
			token = map.get("token");

			return map;
		} catch (RuntimeException e) {
			throw new RuntimeException("Login failed: " + e.getMessage());
		}
	}

    public boolean logout(String userId, String token) {
		return serviceProxy.logout(userId, token);
		}

    public TrainingSession session(String userId, String token, String title, String sport, Double distance,
			Date startDate, Integer duration) {
		return serviceProxy.session(userId, token, title, sport, distance, startDate, duration);
	}

    public List<TrainingSession> sessions(String userId, String token, Date startDate, Date endDate) {
		return serviceProxy.sessions(userId, token, startDate, endDate);
	}

	public Challenge challenge(String userId, String token, String challengeName, Date startDate, Date endDate,
			Double targetTime, Double targetDistance, String sport) {
		return serviceProxy.challenge(userId, token, challengeName, startDate, endDate, targetTime, targetDistance, sport);
	}

	public List<Challenge> challenges(Date startDate, Date endDate, String sport) {
		return serviceProxy.challenges(startDate, endDate, sport);
	}

	public List<Challenge> challengeParticipant(String challengeId, String userId, String token) {
		return serviceProxy.challengeParticipant(challengeId, userId, token);
	}

	public Map<String, Float> challengeStatus(String userId, String token) {
		return serviceProxy.challengeStatus(userId, token);
	}
}