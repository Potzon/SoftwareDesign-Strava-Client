package strava.client.proxies;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> login(Credentials credentials) {
		try {
			 String credentialsJson = objectMapper.writeValueAsString(credentials);
			 
			 HttpRequest request = HttpRequest.newBuilder()
		                .uri(URI.create(BASE_URL + "/users/login"))
		                .header("Content-Type", "application/json")
		                .POST(HttpRequest.BodyPublishers.ofString(credentialsJson))
		                .build();
			 
			 HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


			return switch (response.statusCode()) {
				case 200 -> objectMapper.readValue(response.body(), Map.class); // Successful login, returns token
                case 401 -> throw new RuntimeException("Unauthorized: Invalid credentials");
                default -> throw new RuntimeException("Login failed with status code: " + response.statusCode());
            };
		} catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during login", e);
        }
	}

    @Override
    public boolean logout(String userId, String token) {
    	try {
			URI uri = new URI("http://localhost:8899/users/" + userId + "/logout");
			URL url = uri.toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			// Enviar el token en el cuerpo de la solicitud
			String jsonInputString = "{\"token\": \"" + token + "\"}";

			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				token = null;
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
    }


	@Override
	public TrainingSession session(String userId, String token, String title, String sport, Double distance,
	        Date startDate, Integer duration) {
	    try {
	        // Crear el DTO con los datos de la sesión
	        Map<String, Object> sessionData = Map.of(
	            "token", token,
	            "title", title,
	            "sport", sport,
	            "distance", distance,
	            "startDate", startDate,
	            "duration", duration
	        );

	        // Serializar el DTO a JSON
	        String sessionJson = objectMapper.writeValueAsString(sessionData);

	        // Construir la solicitud HTTP
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/users/" + userId + "/session"))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(sessionJson))
	            .build();

	        // Enviar la solicitud
	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        // Manejo de la respuesta
	        if (response.statusCode() == 201) { // Created
	            // Convertir la respuesta JSON al objeto TrainingSession
	            return objectMapper.readValue(response.body(), TrainingSession.class);
	        } else if (response.statusCode() == 400) {
	            throw new RuntimeException("Bad Request: Invalid session data");
	        } else if (response.statusCode() == 404) {
	            throw new RuntimeException("Not Found: User not found");
	        } else {
	            throw new RuntimeException("Failed to create session with status code: " + response.statusCode());
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error during session creation", e);
	    }
	}


	@Override
	public List<TrainingSession> sessions(String userId, String token, Date startDate, Date endDate) {
	    try {
	        // Crear los parámetros de la consulta
	        StringBuilder uri = new StringBuilder(BASE_URL + "/users/"+userId+"/sessions");
	        
	        // Agregar los parámetros de fecha si están presentes
	        boolean hasParams = false;
	        if (startDate != null) {
	            uri.append("?startDate=").append(URLEncoder.encode(formatDate(startDate), "UTF-8"));
	            hasParams = true;
	        }
	        if (endDate != null) {
	            if (hasParams) {
	                uri.append("&");
	            } else {
	                uri.append("?");
	            }
	            uri.append("endDate=").append(URLEncoder.encode(formatDate(endDate), "UTF-8"));
	        }

	        // Construir la solicitud HTTP
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(uri.toString()))
	            .header("Content-Type", "application/json")
	            .header("Authorization", "Bearer " + token)  // Usar token en el header Authorization
	            .GET()
	            .build();

	        // Enviar la solicitud
	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        // Manejo de la respuesta
	        if (response.statusCode() == 200) { // OK
	            // Convertir la respuesta JSON a una lista de TrainingSession
	            return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, TrainingSession.class));
	        } else if (response.statusCode() == 400) {
	            throw new RuntimeException("Bad Request: Invalid token or parameters");
	        } else if (response.statusCode() == 404) {
	            throw new RuntimeException("Not Found: User not found");
	        } else {
	            throw new RuntimeException("Failed to retrieve sessions with status code: " + response.statusCode());
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error during session query", e);
	    }
	}
	private String formatDate(Date date) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(date);
	}


	@Override
	public Challenge challenge(String userId, String token, String challengeName, Date startDate, Date endDate,
	                           Double targetTime, Double targetDistance, String sport) {
	    try {
	        URI uri = URI.create(BASE_URL + "/challenges/users/" + userId + "/challenge");
	        String jsonBody = """
	            {
	                "token": "%s",
	                "challengeName": "%s",
	                "startDate": "%s",
	                "endDate": "%s",
	                "targetTime": %.2f,
	                "targetDistance": %.2f,
	                "sport": "%s"
	            }
	            """.formatted(
	            token,
	            challengeName,
	            formatDate(startDate),
	            formatDate(endDate),
	            targetTime,
	            targetDistance,
	            sport
	        );

	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(uri)
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
	                .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        switch (response.statusCode()) {
	            case 201 -> {
	                return objectMapper.readValue(response.body(), Challenge.class);
	            }
	            case 400 -> throw new RuntimeException("Bad Request: Invalid challenge data");
	            case 404 -> throw new RuntimeException("Not Found: User not found");
	            case 500 -> throw new RuntimeException("Internal server error while creating the challenge");
	            default -> throw new RuntimeException("Failed to create challenge. Status code: " + response.statusCode());
	        }
	    } catch (IOException | InterruptedException e) {
	        throw new RuntimeException("Error while creating the challenge", e);
	    }
	}



	@Override
	public List<Challenge> challenges(Date startDate, Date endDate, String sport) {
	    try {
	        StringBuilder uri = new StringBuilder(BASE_URL + "/challenges");
	        boolean hasParams = false;
	        if (startDate != null) {
	            uri.append("?startDate=").append(URLEncoder.encode(formatDate(startDate), "UTF-8"));
	            hasParams = true;
	        }
	        if (endDate != null) {
	            uri.append(hasParams ? "&" : "?");
	            uri.append("endDate=").append(URLEncoder.encode(formatDate(endDate), "UTF-8"));
	            hasParams = true;
	        }
	        if (sport != null && !sport.isEmpty()) {
	            uri.append(hasParams ? "&" : "?");
	            uri.append("sport=").append(URLEncoder.encode(sport, "UTF-8"));
	        }
	        
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(uri.toString()))
	            .header("Content-Type", "application/json")
	            .GET()
	            .build();
	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	        return switch (response.statusCode()) {
	            case 200 -> objectMapper.readValue(
	                    response.body(),
	                    objectMapper.getTypeFactory().constructCollectionType(List.class, Challenge.class)
	            );
	            case 204 -> throw new RuntimeException("No Content: No challenges found");
	            case 500 -> throw new RuntimeException("Internal server error while fetching challenges");
	            default -> throw new RuntimeException("Failed to fetch challenges with status code: " + response.statusCode());
	        };
	    } catch (IOException | InterruptedException e) {
	        throw new RuntimeException("Error while fetching challenges", e);
	    }
	}

	
	@Override
	public List<Challenge> challengeParticipant(String challengeId, String userId, String token) {
	    try {
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(BASE_URL + "/challenges/users/" + userId + "/challenges/" + challengeId))
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(token))
	                .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        return switch (response.statusCode()) {
	            case 200 -> objectMapper.readValue(response.body(),
	                    objectMapper.getTypeFactory().constructCollectionType(List.class, Challenge.class));
	            case 204 -> throw new RuntimeException("No Content: User not participating in any challenges");
	            case 400 -> throw new RuntimeException("Bad Request: Invalid challenge or user ID");
	            case 404 -> throw new RuntimeException("Not Found: Challenge or user not found");
	            case 500 -> throw new RuntimeException("Internal server error while fetching user challenges");
	            default -> throw new RuntimeException("Failed to fetch user challenges with status code: " + response.statusCode());
	        };
	    } catch (IOException | InterruptedException e) {
	        throw new RuntimeException("Error while fetching user challenges", e);
	    }
	}
	
	@Override
	public Map<String, Float> challengeStatus(String userId, String token) {
	    try {
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(BASE_URL + "/challenges/users/" + userId + "/challenges/status"))
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(token))
	                .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        return switch (response.statusCode()) {
	            case 200 -> objectMapper.readValue(response.body(),
	                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Float.class));
	            case 204 -> throw new RuntimeException("No Content: No challenges with progress found");
	            case 400 -> throw new RuntimeException("Bad Request: Invalid user ID or token");
	            case 404 -> throw new RuntimeException("Not Found: User or challenge not found");
	            case 500 -> throw new RuntimeException("Internal server error while fetching challenge status");
	            default -> throw new RuntimeException("Failed to fetch challenge status with status code: " + response.statusCode());
	        };
	    } catch (IOException | InterruptedException e) {
	        throw new RuntimeException("Error while fetching challenge status", e);
	    }
	}

	}
	

