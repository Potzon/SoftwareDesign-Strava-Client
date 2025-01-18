package strava.client.data;

import java.util.Date;

public record TrainingSession(
		String sessionId,
	    String title,
	    String sport,
	    Float distance,
	    Date startDate,
	    Float duration
	    ) {
	
	@Override
	public String toString() {
	    return String.format("%s - %s - %.2f km - %s", title(), sport(), distance(), startDate());
	}
}
