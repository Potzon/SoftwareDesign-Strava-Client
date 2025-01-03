package strava.client.data;

public record Credentials(
		String email,
		String password,
		String externalProvider
		) {}
