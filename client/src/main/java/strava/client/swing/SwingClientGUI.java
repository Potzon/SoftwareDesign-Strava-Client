package strava.client.swing;

import javax.swing.*;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.io.*;
import java.net.HttpURLConnection;

public class SwingClientGUI {

	private static String userId;
	private static String token;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame initialFrame = createInitialFrame();
			initialFrame.setVisible(true);
		});
	}

	private static JFrame createInitialFrame() {
		JFrame frame = new JFrame("API Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 200);

		JPanel panel = new JPanel(new GridLayout(2, 1));

		JButton loginButton = new JButton("Login");
		JButton registerButton = new JButton("Register");

		panel.add(loginButton);
		panel.add(registerButton);

		frame.add(panel);

		// Acción para el botón de Login
		loginButton.addActionListener(e -> openLoginWindow(frame));

		// Acción para el botón de Register
		registerButton.addActionListener(e -> openRegisterWindow());

		return frame;
	}

	// Método para abrir la ventana de Login
	private static void openLoginWindow(JFrame initialFrame) {
		JFrame loginFrame = new JFrame("Login");
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		loginFrame.setSize(400, 300);

		JPanel loginPanel = new JPanel(new GridLayout(7, 1));

		JLabel emailLabel = new JLabel("Email:");
		JTextField emailField = new JTextField("user1@example.com");
		loginPanel.add(emailLabel);
		loginPanel.add(emailField);

		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordField = new JPasswordField("password123");
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordField);

		JLabel providerLabel = new JLabel("External Provider:");
		String[] providers = { "Google", "Facebook" };
		JComboBox<String> providerComboBox = new JComboBox<>(providers);
		loginPanel.add(providerLabel);
		loginPanel.add(providerComboBox);

		JButton loginButton = new JButton("Login");
		loginPanel.add(loginButton);

		loginFrame.add(loginPanel);
		loginFrame.setVisible(true);

		loginButton.addActionListener(e -> {
			String email = emailField.getText();
			String password = new String(passwordField.getPassword());
			String externalProvider = (String) providerComboBox.getSelectedItem();

			if (externalProvider != null) {
				externalProvider = externalProvider.toLowerCase();
			}

			try {
				// Llamada al servicio de login
				URI uri = new URI("http://localhost:8899/users/login");
				URL url = uri.toURL();
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				String jsonInputString = String.format(
						"{\"email\": \"%s\", \"password\": \"%s\", \"externalProvider\": \"%s\"}", email, password,
						externalProvider);

				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					try (BufferedReader br = new BufferedReader(
							new InputStreamReader(connection.getInputStream(), "utf-8"))) {
						StringBuilder response = new StringBuilder();
						String responseLine;
						while ((responseLine = br.readLine()) != null) {
							response.append(responseLine.trim());
						}

						// Extraer el token y userId
						String token = extractToken(response.toString());
						if (token != null) {
							SwingClientGUI.token = token;
							userId = extractUserId(response.toString());
							JOptionPane.showMessageDialog(loginFrame, "Login Successful: Token received");
							loginFrame.dispose();
							openAPIMenuWindow(initialFrame);
						} else {
							JOptionPane.showMessageDialog(loginFrame, "Error: Token not found");
						}
					}
				} else {
					JOptionPane.showMessageDialog(loginFrame, "Login Failed: " + responseCode);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(loginFrame, "Error: " + ex.getMessage());
			}
		});
	}

	// Método para abrir el menú de APIs
	private static void openAPIMenuWindow(JFrame initialFrame) {
		initialFrame.setVisible(false); // Ocultar la ventana inicial

		JFrame menuFrame = new JFrame("API Menu");
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setSize(400, 300);

		JPanel menuPanel = new JPanel(new GridLayout(5, 1));

		JButton logoutButton = new JButton("Logout");
		JButton createSessionButton = new JButton("Create Training Session");
		JButton querySessionsButton = new JButton("Query Training Sessions");
		JButton setupChallengeButton = new JButton("Set Up Challenge");
		JButton queryChallengesButton = new JButton("Query Challenges");

		menuPanel.add(logoutButton);
		menuPanel.add(createSessionButton);
		menuPanel.add(querySessionsButton);
		menuPanel.add(setupChallengeButton);
		menuPanel.add(queryChallengesButton);

		menuFrame.add(menuPanel);
		menuFrame.setVisible(true);

		logoutButton.addActionListener(e -> logout(menuFrame, initialFrame));
		createSessionButton.addActionListener(e -> openCreateSessionWindow());
		querySessionsButton.addActionListener(e -> querySessions());
		setupChallengeButton.addActionListener(e -> setupChallenge());
		queryChallengesButton.addActionListener(e -> queryChallenges());
	}

	// Método para realizar logout
	private static void logout(JFrame menuFrame, JFrame initialFrame) {
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
				JOptionPane.showMessageDialog(menuFrame, "Logout Successful");
				menuFrame.dispose();
				initialFrame.setVisible(true); // Volver a mostrar la ventana inicial
			} else {
				JOptionPane.showMessageDialog(menuFrame, "Logout Failed: " + responseCode);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(menuFrame, "Error: " + ex.getMessage());
		}
	}

	// Método para extraer el token de la respuesta
	private static String extractToken(String response) {
		try {
			int tokenStartIndex = response.indexOf("\"token\":\"") + 9; // Encontramos el índice donde comienza el token
			int tokenEndIndex = response.indexOf("\"", tokenStartIndex); // Encontramos el índice donde termina el token
			if (tokenStartIndex != -1 && tokenEndIndex != -1) {
				return response.substring(tokenStartIndex, tokenEndIndex); // Extraemos el token
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Si no se encuentra el token, devolvemos null
	}

	// Método para extraer el userId de la respuesta
	private static String extractUserId(String response) {
		try {
			int userIdStartIndex = response.indexOf("\"userId\":\"") + 10; // Encontramos el índice donde comienza el
																			// userId
			int userIdEndIndex = response.indexOf("\"", userIdStartIndex); // Encontramos el índice donde termina el
																			// userId
			if (userIdStartIndex != -1 && userIdEndIndex != -1) {
				return response.substring(userIdStartIndex, userIdEndIndex); // Extraemos el userId
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Si no se encuentra el userId, devolvemos null
	}

	// Métodos simulados para otras funcionalidades
	private static void openCreateSessionWindow() {
		JFrame createSessionFrame = new JFrame("Create Training Session");
		createSessionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createSessionFrame.setSize(400, 600);

		JPanel panel = new JPanel(new GridLayout(8, 2));

		// Campos
		panel.add(new JLabel("User ID:"));
		JTextField userIdField = new JTextField(userId);
		panel.add(userIdField);

		panel.add(new JLabel("Token:"));
		JTextField tokenField = new JTextField(token);
		panel.add(tokenField);

		panel.add(new JLabel("Title:"));
		JTextField titleField = new JTextField();
		panel.add(titleField);

		panel.add(new JLabel("Sport:"));
		JTextField sportField = new JTextField();
		panel.add(sportField);

		panel.add(new JLabel("Distance (km):"));
		JTextField distanceField = new JTextField();
		panel.add(distanceField);

		panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
		JTextField startDateField = new JTextField();
		panel.add(startDateField);

		panel.add(new JLabel("Duration (minutes):"));
		JTextField durationField = new JTextField();
		panel.add(durationField);

		JButton createButton = new JButton("Create Session");
		panel.add(createButton);

		createSessionFrame.add(panel);
		createSessionFrame.setVisible(true);

		createButton.addActionListener(e -> {
			String userId = userIdField.getText();
			String token = tokenField.getText();
			String title = titleField.getText();
			String sport = sportField.getText();
			String distance = distanceField.getText();
			String startDate = startDateField.getText();
			String duration = durationField.getText();

			try {
				URI uri = new URI("http://localhost:8899/sessions/users/" + userId + "/session");
				URL url = uri.toURL();
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				String jsonInputString = String.format(
						"{\"token\": \"%s\", \"title\": \"%s\", \"sport\": \"%s\", \"distance\": %s, \"startDate\": \"%s\", \"duration\": %s}",
						token, title, sport, distance, startDate, duration);

				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_CREATED) {
					JOptionPane.showMessageDialog(createSessionFrame, "Session Created Successfully");
					createSessionFrame.dispose();
				} else {
					JOptionPane.showMessageDialog(createSessionFrame, "Failed to Create Session: " + responseCode);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(createSessionFrame, "Error: " + ex.getMessage());
			}
		});
	}

	private static void querySessions() {
		JFrame queryFrame = new JFrame("Query Training Sessions");
		queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		queryFrame.setSize(400, 300);

		JPanel panel = new JPanel(new GridLayout(5, 2));

		panel.add(new JLabel("User ID:"));
		JTextField userIdField = new JTextField(userId);
		panel.add(userIdField);

		panel.add(new JLabel("Token:"));
		JTextField tokenField = new JTextField(token);
		panel.add(tokenField);

		panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
		JTextField startDateField = new JTextField();
		panel.add(startDateField);

		panel.add(new JLabel("End Date (yyyy-MM-dd):"));
		JTextField endDateField = new JTextField();
		panel.add(endDateField);

		JButton queryButton = new JButton("Query Sessions");
		panel.add(queryButton);

		queryFrame.add(panel);
		queryFrame.setVisible(true);

		queryButton.addActionListener(e -> {
			String userId = userIdField.getText();
			String token = tokenField.getText();
			String startDate = startDateField.getText();
			String endDate = endDateField.getText();

			try {
				URI uri = new URI("http://localhost:8899/sessions/users/" + userId + "/sessions?startDate=" + startDate
						+ "&endDate=" + endDate);
				URL url = uri.toURL();
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");
				connection.setRequestProperty("Authorization", "Bearer " + token);

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuilder response = new StringBuilder();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					JOptionPane.showMessageDialog(queryFrame, "Sessions: " + response.toString());
				} else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
					JOptionPane.showMessageDialog(queryFrame, "No sessions found.");
				} else {
					JOptionPane.showMessageDialog(queryFrame, "Failed to Query Sessions: " + responseCode);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(queryFrame, "Error: " + ex.getMessage());
			}
		});
	}

	private static void setupChallenge() {
		JFrame createChallengeFrame = new JFrame("Set Up Challenge");
		createChallengeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createChallengeFrame.setSize(400, 600);

		JPanel panel = new JPanel(new GridLayout(7, 2));

		panel.add(new JLabel("Challenge Name:"));
		JTextField challengeNameField = new JTextField();
		panel.add(challengeNameField);

		panel.add(new JLabel("Sport:"));
		JTextField sportField = new JTextField();
		panel.add(sportField);

		panel.add(new JLabel("Target Distance (km):"));
		JTextField targetDistanceField = new JTextField();
		panel.add(targetDistanceField);

		panel.add(new JLabel("Target Time (minutes):"));
		JTextField targetTimeField = new JTextField();
		panel.add(targetTimeField);

		panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
		JTextField startDateField = new JTextField();
		panel.add(startDateField);

		panel.add(new JLabel("End Date (yyyy-MM-dd):"));
		JTextField endDateField = new JTextField();
		panel.add(endDateField);

		JButton createButton = new JButton("Create Challenge");
		panel.add(createButton);

		createChallengeFrame.add(panel);
		createChallengeFrame.setVisible(true);

		createButton.addActionListener(e -> {
			String challengeName = challengeNameField.getText();
			String sport = sportField.getText();
			String targetDistance = targetDistanceField.getText();
			String targetTime = targetTimeField.getText();
			String startDate = startDateField.getText();
			String endDate = endDateField.getText();

			try {
				URI uri = new URI("http://localhost:8899/challenges/users/" + userId + "/challenge");
				URL url = uri.toURL();
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				String jsonInputString = String.format(
						"{\"token\": \"%s\", \"challengeName\": \"%s\", \"sport\": \"%s\", \"targetDistance\": %s, \"targetTime\": %s, \"startDate\": \"%s\", \"endDate\": \"%s\"}",
						token, challengeName, sport, targetDistance, targetTime, startDate, endDate);

				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_CREATED) {
					JOptionPane.showMessageDialog(createChallengeFrame, "Challenge Created Successfully");
					createChallengeFrame.dispose();
				} else {
					JOptionPane.showMessageDialog(createChallengeFrame, "Failed to Create Challenge: " + responseCode);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(createChallengeFrame, "Error: " + ex.getMessage());
			}
		});
	}

	private static void queryChallenges() {
		JFrame queryChallengeFrame = new JFrame("Query Challenges");
		queryChallengeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		queryChallengeFrame.setSize(400, 300);

		JPanel panel = new JPanel(new GridLayout(4, 2));

		panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
		JTextField startDateField = new JTextField();
		panel.add(startDateField);

		panel.add(new JLabel("End Date (yyyy-MM-dd):"));
		JTextField endDateField = new JTextField();
		panel.add(endDateField);

		panel.add(new JLabel("Sport (optional):"));
		JTextField sportField = new JTextField();
		panel.add(sportField);

		JButton queryButton = new JButton("Query Challenges");
		panel.add(queryButton);

		queryChallengeFrame.add(panel);
		queryChallengeFrame.setVisible(true);

		queryButton.addActionListener(e -> {
			String startDate = startDateField.getText();
			String endDate = endDateField.getText();
			String sport = sportField.getText();

			try {
				String urlString = "http://localhost:8899/challenges?startDate=" + startDate + "&endDate=" + endDate;
				if (!sport.isBlank()) {
					urlString += "&sport=" + sport;
				}

				URI uri = new URI(urlString);
				URL url = uri.toURL();
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuilder response = new StringBuilder();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					JOptionPane.showMessageDialog(queryChallengeFrame, "Challenges: " + response.toString());
				} else {
					JOptionPane.showMessageDialog(queryChallengeFrame, "Failed to Query Challenges: " + responseCode);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(queryChallengeFrame, "Error: " + ex.getMessage());
			}
		});
	}

	// Método para abrir la ventana de Register
	private static void openRegisterWindow() {
		JFrame registerFrame = new JFrame("Register");
		registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		registerFrame.setSize(400, 600);

		JPanel registerPanel = new JPanel(new GridLayout(12, 2));

		// Campos para el formulario
		registerPanel.add(new JLabel("Email:"));
		JTextField emailField = new JTextField();
		registerPanel.add(emailField);

		registerPanel.add(new JLabel("Password:"));
		JPasswordField passwordField = new JPasswordField();
		registerPanel.add(passwordField);

		registerPanel.add(new JLabel("Name:"));
		JTextField nameField = new JTextField();
		registerPanel.add(nameField);

		registerPanel.add(new JLabel("Birthdate (yyyy-MM-dd):"));
		JTextField birthdateField = new JTextField();
		registerPanel.add(birthdateField);

		registerPanel.add(new JLabel("Weight (kg):"));
		JTextField weightField = new JTextField();
		registerPanel.add(weightField);

		registerPanel.add(new JLabel("Height (cm):"));
		JTextField heightField = new JTextField();
		registerPanel.add(heightField);

		registerPanel.add(new JLabel("Max Heart Rate:"));
		JTextField maxHeartRateField = new JTextField();
		registerPanel.add(maxHeartRateField);

		registerPanel.add(new JLabel("Rest Heart Rate:"));
		JTextField restHeartRateField = new JTextField();
		registerPanel.add(restHeartRateField);

		JButton registerButton = new JButton("Register");
		registerPanel.add(registerButton);

		registerFrame.add(registerPanel);
		registerFrame.setVisible(true);

		registerButton.addActionListener(e -> {
			String email = emailField.getText();
			String password = new String(passwordField.getPassword());
			String name = nameField.getText();
			String birthdate = birthdateField.getText();
			String weight = weightField.getText();
			String height = heightField.getText();
			String maxHeartRate = maxHeartRateField.getText();
			String restHeartRate = restHeartRateField.getText();

			try {
				URI uri = new URI("http://localhost:8899/users/user");
				URL url = uri.toURL();
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				// Crear el JSON con los parámetros necesarios
				String jsonInputString = String.format(
						"{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\", \"birthdate\": \"%s\", \"weight\": %s, \"height\": %s, \"maxHeartRate\": %s, \"restHeartRate\": %s}",
						email, password, name, birthdate, weight, height, maxHeartRate, restHeartRate);

				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_CREATED) {
					JOptionPane.showMessageDialog(registerFrame, "Registration Successful");
					registerFrame.dispose();
				} else {
					JOptionPane.showMessageDialog(registerFrame, "Registration Failed: " + responseCode);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(registerFrame, "Error: " + ex.getMessage());
			}
		});
	}

}