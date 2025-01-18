package strava.client.proxies;

import java.util.Date;
import java.util.List;
import java.util.Map;

import strava.client.data.Challenge;
import strava.client.data.Credentials;
import strava.client.data.TrainingSession;
import strava.client.data.User;

public interface IStravaServiceProxy {
	
	//user methods
    User user(User user);
    Map<String, String> login(Credentials credentials);
    boolean logout(String userId, String token);
    
    //training sessions methods
    TrainingSession session(String userId, String token, String title, String sport, Float distance, Date startDate, Float duration);
    List<TrainingSession> sessions(String UserId, String token, Date startDate, Date endDate);
    
    //challenge methods
    Challenge challenge(String userId, String token, String challengeName, Date startDate, Date endDate, int targetTime, Float targetDistance, String sport);
    List<Challenge> challenges(Date startDate, Date endDate, String sport);
    List<Challenge> challengeParticipant(String challengeId, String userId, String token);
    Map<String, Integer> challengeStatus(String userId, String token);
}
