package strava.client.proxies;

import java.net.http.HttpClient;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import strava.client.data.Challenge;
import strava.client.data.Credentials;
import strava.client.data.TrainingSession;
import strava.client.data.User;

public class HttpServiceProxy implements IStravaServiceProxy{

	private static final String BASE_URL = "http://localhost:8899";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpServiceProxy() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
	@Override
	public User user(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> login(Credentials credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> logout(String userId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrainingSession session(String userId, String token, String title, String sport, Double distance,
			Date startDate, Integer duration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TrainingSession> sessions(String token, Date startDate, Date endDate) {
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
