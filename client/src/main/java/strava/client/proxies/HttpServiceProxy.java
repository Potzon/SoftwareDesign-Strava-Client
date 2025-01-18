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
import java.util.ArrayList;
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
        try {
            // Convertir User a JSON
            String userJson = objectMapper.writeValueAsString(user);
            System.out.println("User JSON: " + userJson); 


            // Crear solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
            	    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            return switch (response.statusCode()) {
			case 201 -> objectMapper.readValue(response.body(), User.class);
            case 401 -> throw new RuntimeException("Unauthorized: Invalid credentials");
            default -> throw new RuntimeException("Login failed with status code: " + response.statusCode());
        };
        } catch (Exception ex) {
            throw new RuntimeException("Error during registration: " + ex.getMessage(), ex);
        }
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
	        // Crear el cuerpo de la solicitud como JSON
	        String jsonInputString = "{\"token\": \"" + token + "\"}";
	        
	        // Construir la solicitud HTTP
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/users/" + userId + "/logout"))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
	            .build();

	        // Enviar la solicitud y obtener la respuesta
	        HttpResponse<Void> response = HttpClient.newHttpClient()
	            .send(request, HttpResponse.BodyHandlers.discarding());

	        // Verificar el código de respuesta
	        if (response.statusCode() == 200) {
	            return true;
	        } else {
	            return false;
	        }
	    } catch (Exception ex) {
	        return false;
	    }
	}



	@Override
	public TrainingSession session(String userId, String token, String title, String sport, Float distance,
	        Date startDate, Float duration) {
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
	        System.out.println("Session JSON: " + sessionData); 
	        
	        // Construir la solicitud HTTP
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/sessions/users/" + userId + "/session"))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(sessionJson))
	            .build();

	        // Enviar la solicitud
	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	        

            System.out.println("Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

	        // Manejo de la respuesta
	        if (response.statusCode() == 201) { // Created
	        	Map<String, Object> responseData = objectMapper.readValue(response.body(), Map.class);
	            return new TrainingSession(
	                (String) responseData.get("sessionId"),
	                (String) responseData.get("title"),
	                (String) responseData.get("sport"),
	                ((Number) responseData.get("distance")).floatValue(),
	                objectMapper.convertValue(responseData.get("startDate"), Date.class),
	                ((Number) responseData.get("duration")).floatValue()
	            );
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

	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        String formattedStartDate = dateFormat.format(startDate);
	        String formattedEndDate = dateFormat.format(endDate);
	        
	    	System.out.println(startDate + " " + endDate);
	    	System.out.println(formattedStartDate + " " + formattedEndDate);
	        // Construir la URL con los parámetros de consulta
	        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/sessions/users/" + userId + "/sessions");
	        
	        boolean hasParams = false;
	        if (startDate != null) {
	            urlBuilder.append("?startDate=").append(formattedStartDate);
	            hasParams = true;
	        }
	        if (endDate != null) {
	            urlBuilder.append(hasParams ? "&" : "?").append("endDate=").append(formattedEndDate);
	        }

	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(urlBuilder.toString()))
	            .header("Content-Type", "application/json")
	            .method("GET", HttpRequest.BodyPublishers.ofString(String.valueOf(token))) 
	            .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        if (response.statusCode() == 200) {
	            // Parsear el cuerpo de la respuesta a una lista de sesiones
	            List<Map<String, Object>> responseData = objectMapper.readValue(response.body(), List.class);
	            List<TrainingSession> sessions = new ArrayList<>();
	            for (Map<String, Object> sessionData : responseData) {
	                sessions.add(new TrainingSession(
	                    (String) sessionData.get("sessionId"),
	                    (String) sessionData.get("title"),
	                    (String) sessionData.get("sport"),
	                    ((Number) sessionData.get("distance")).floatValue(),
	                    objectMapper.convertValue(sessionData.get("startDate"), Date.class),
	                    ((Number) sessionData.get("duration")).floatValue()
	                ));
	            }
	            return sessions;
	        } else {
	            throw new RuntimeException("Error: " + response.statusCode() + " - " + response.body());
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error during sessions query", e);
	    }
	}






	@Override
	public Challenge challenge(String userId, String token, String challengeName, Date startDate, Date endDate,
			int targetTime, Float targetDistance, String sport) {
		try {
	        Map<String, Object> challengeData = Map.of(
	            "token", token,
	            "challengeName", challengeName,
	            "startDate", startDate,
	            "endDate", endDate,
	            "targetTime", targetTime,
	            "targetDistance", targetDistance,
	            "sport", sport
	        );

	        String challengeJson = objectMapper.writeValueAsString(challengeData);
	        System.out.println("Challenge JSON: " + challengeData);

	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/challenges/users/" + userId + "/challenge"))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(challengeJson))
	            .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        System.out.println("Response code: " + response.statusCode());
	        System.out.println("Response body: " + response.body());

	        if (response.statusCode() == 201) { // Created
	            Map<String, Object> responseData = objectMapper.readValue(response.body(), Map.class);
	            return new Challenge(
	                (String) responseData.get("challengeId"),
	                (String) responseData.get("challengeName"),
	                objectMapper.convertValue(responseData.get("startDate"), Date.class),
	                objectMapper.convertValue(responseData.get("endDate"), Date.class),
	                (Integer) responseData.get("targetTime"),
	                ((Number) responseData.get("targetDistance")).floatValue(),
	                (String) responseData.get("sport"),
	                userId
	            );
	        } else if (response.statusCode() == 400) {
	            throw new RuntimeException("Bad Request: Invalid challenge data");
	        } else if (response.statusCode() == 404) {
	            throw new RuntimeException("Not Found: User not found");
	        } else {
	            throw new RuntimeException("Failed to create challenge with status code: " + response.statusCode());
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error during challenge creation", e);
	    }
	}



	@Override
	public List<Challenge> challenges(Date startDate, Date endDate, String sport) {
		try {

	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        String formattedStartDate = dateFormat.format(startDate);
	        String formattedEndDate = dateFormat.format(endDate);
	        
	    	System.out.println(startDate + " " + endDate + " " + sport);
	    	System.out.println(formattedStartDate + " " + formattedEndDate);
	        // Construir la URL con los parámetros de consulta
	        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/challenges/challenges");
	        
	        boolean hasParams = false;
	        if (startDate != null) {
	            urlBuilder.append("?startDate=").append(formattedStartDate);
	            hasParams = true;
	        }
	        if (endDate != null) {
	            urlBuilder.append(hasParams ? "&" : "?").append("endDate=").append(formattedEndDate);
	            hasParams = true;
	        }
	        if (sport != null) {
	            urlBuilder.append(hasParams ? "&" : "?").append("Sport=").append(sport);
	        }

	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(urlBuilder.toString()))
	            .header("Content-Type", "application/json")
	            .GET()
	            .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        if (response.statusCode() == 200) {
	            // Parsear el cuerpo de la respuesta a una lista de sesiones
	            List<Map<String, Object>> responseData = objectMapper.readValue(response.body(), List.class);
	            List<Challenge> challenges = new ArrayList<>();
	            for (Map<String, Object> challengeData : responseData) {
	            	challenges.add(new Challenge(
	                    (String) challengeData.get("challengeId"),
	                    (String) challengeData.get("challengeName"),
	                    objectMapper.convertValue(challengeData.get("startDate"), Date.class),
	                    objectMapper.convertValue(challengeData.get("endDate"), Date.class),
	                    ((Number) challengeData.get("targetTime")).intValue(),
	                    ((Number) challengeData.get("targetDistance")).floatValue(),
	                    (String) challengeData.get("sport"),
	                    (String) challengeData.get("userId")
	                ));
	            }
	            return challenges;
	        } else {
	            throw new RuntimeException("Error: " + response.statusCode() + " - " + response.body());
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error during challenges query", e);
	    }
	}

	
	@Override
	public List<Challenge> challengeParticipant(String challengeId, String userId, String token) {
	    try {
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(BASE_URL + "/challenges/users/" + userId + "/challenges/" + challengeId))
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(token)))
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
	        throw new RuntimeException("Error while fetching challenge", e);
	    }
	}
	
	@Override
	public Map<String, Integer> challengeStatus(String userId, String token) {
	    try {
	        // Usar GET en lugar de POST
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(BASE_URL + "/challenges/users/" + userId + "/challenges/status?token=" + token))  // Token como parámetro
	                .header("Content-Type", "application/json")
	                .method("GET", HttpRequest.BodyPublishers.ofString(String.valueOf(token))) 
	                .build();
	        

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        System.out.println("Response code: " + response.statusCode());
	        System.out.println("Response body: " + response.body());

	        return switch (response.statusCode()) {
	            case 200 -> objectMapper.readValue(response.body(),
	                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));  
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
	

