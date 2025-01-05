package strava.client.data;

import java.util.Date;
import java.util.List;


public record User(
		String userId,
		String name,
	    String email,
	    String password,
	    Date birthdate,
	    Integer weight,
	    Integer height,
	    Float maxHeartRate,
	    Float restHeartRate,
	    List<Challenge> acceptedChallenges,
	    List<TrainingSession> trainingSessions) {}

