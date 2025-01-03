package strava.client.data;

import java.util.Date;

public record TrainingSession(
		String token,
	    String title,
	    String sport,
	    Float distance,
	    Date startDate,
	    Float duration
	    ) {}
