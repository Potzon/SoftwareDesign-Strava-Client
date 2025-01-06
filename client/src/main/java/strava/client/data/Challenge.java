package strava.client.data;

import java.util.Date;

public record Challenge(
		String challengeId,
	    String challengeName,
	    Date startDate,
	    Date endDate,
	    Integer targetTime,
	    Float targetDistance,
	    String sport,
	    String userId
	    ) {}
