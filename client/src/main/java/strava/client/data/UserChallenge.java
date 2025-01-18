package strava.client.data;

public record UserChallenge(
		String id,
		User user,
		Challenge challenge,
	    Integer progress) {
	
}
