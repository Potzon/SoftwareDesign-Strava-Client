package strava.client.data;

import java.util.Date;

public record TrainingSession(
		String sessionId,
		String userId,
	    String title,
	    String sport,
	    Float distance,
	    Date startDate,
	    Float duration
	    ) {}
