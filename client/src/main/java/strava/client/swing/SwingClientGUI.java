package strava.client.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import strava.client.data.Challenge;
import strava.client.data.Credentials;
import strava.client.data.TrainingSession;
import strava.client.data.User;
import java.util.List;
public class SwingClientGUI extends JFrame{
    private static SwingClientController controller = new SwingClientController();
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

        loginButton.addActionListener(e -> openLoginWindow(frame));
        registerButton.addActionListener(e -> openRegisterWindow());

        return frame;
    }

    private static void openLoginWindow(JFrame initialFrame) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(400, 300);

        JPanel loginPanel = new JPanel(new GridLayout(5, 1));

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
            String externalProv = providerComboBox.getSelectedItem().toString();

            // Validación de campos vacíos
            if (email.isEmpty() || password.isEmpty() || externalProv.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please, fill in the blank spaces", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Credentials credentials = new Credentials(email, password, externalProv);
                var response = controller.login(credentials);

                // Validación de la respuesta
                if (response == null || !response.containsKey("userId") || !response.containsKey("token")) {
                    JOptionPane.showMessageDialog(loginFrame, "Error: Invalid Server Response.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                userId = response.get("userId");
                token = response.get("token");

                JOptionPane.showMessageDialog(loginFrame, "Login Successful:\nUser ID: " + userId + ", Token: " + token);
                loginFrame.dispose();
                openAPIMenuWindow(initialFrame);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void openAPIMenuWindow(JFrame initialFrame) {
        initialFrame.setVisible(false);

        JFrame menuFrame = new JFrame("API Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 400);

        JPanel menuPanel = new JPanel(new GridLayout(7, 1));

        JButton logoutButton = new JButton("Logout");
        JButton createSessionButton = new JButton("Create a training session");
        JButton querySessionsButton = new JButton("Query training sessions");
        JButton setupChallengeButton = new JButton("Set up a challenge");
        JButton queryChallengesButton = new JButton("Query challenges");
        JButton addChallengeButton = new JButton("Participate on a challenge");
        JButton challengeStatusButton = new JButton("Challenges status");

        menuPanel.add(logoutButton);
        menuPanel.add(createSessionButton);
        menuPanel.add(querySessionsButton);
        menuPanel.add(setupChallengeButton);
        menuPanel.add(queryChallengesButton);
        menuPanel.add(addChallengeButton);
        menuPanel.add(challengeStatusButton);

        menuFrame.add(menuPanel);
        menuFrame.setVisible(true);

        logoutButton.addActionListener(e -> logout(menuFrame, initialFrame));
        createSessionButton.addActionListener(e -> openCreateSessionWindow());
		querySessionsButton.addActionListener(e -> querySessions());
		setupChallengeButton.addActionListener(e -> setupChallenge());
		queryChallengesButton.addActionListener(e -> queryChallenges());
		setupChallengeButton.addActionListener(e -> addChallenge());
		queryChallengesButton.addActionListener(e -> challengesStatus());
       
    }
    

	private static void openRegisterWindow() {
 		JFrame registerFrame = new JFrame("Register");
 		registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 		registerFrame.setSize(400, 600);

 		JPanel registerPanel = new JPanel(new GridLayout(12, 2));

 		// Campos para el formulario
 		registerPanel.add(new JLabel("Email:"));
 		JTextField emailField = new JTextField("god@gmail.com");
 		registerPanel.add(emailField);

 		registerPanel.add(new JLabel("Password:"));
 		JPasswordField passwordField = new JPasswordField("123");
 		registerPanel.add(passwordField);

 		registerPanel.add(new JLabel("Name:"));
 		JTextField nameField = new JTextField("God");
 		registerPanel.add(nameField);

 		registerPanel.add(new JLabel("Birthdate (yyyy-MM-dd):"));
 		JTextField birthdateField = new JTextField("2004-02-29");
 		registerPanel.add(birthdateField);

 		registerPanel.add(new JLabel("Weight (kg):"));
 		JTextField weightField = new JTextField("50");
 		registerPanel.add(weightField);

 		registerPanel.add(new JLabel("Height (cm):"));
 		JTextField heightField = new JTextField("180");
 		registerPanel.add(heightField);

 		registerPanel.add(new JLabel("Max Heart Rate:"));
 		JTextField maxHeartRateField = new JTextField("150");
 		registerPanel.add(maxHeartRateField);

 		registerPanel.add(new JLabel("Rest Heart Rate:"));
 		JTextField restHeartRateField = new JTextField("81");
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
 			
 			Date parsedDate = null;
			try {
				parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthdate);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
 			try {
 				User user = new User(null, name, email, password,parsedDate, Integer.parseInt(weight), Integer.parseInt(height), Float.parseFloat(maxHeartRate), Float.parseFloat(restHeartRate), null, null);
 				User registeredUser = controller.user(user);

 		        if (registeredUser != null) {
 		            JOptionPane.showMessageDialog(registerFrame, "User registered successfully!");
 		            registerFrame.dispose();
 		        } else {
 		            JOptionPane.showMessageDialog(registerFrame, "Registration failed.");
 		        }
 		    } catch (NumberFormatException ex) {
 		        JOptionPane.showMessageDialog(registerFrame, "Invalid numeric value. Please check the fields.");
 		    } catch (Exception ex) {
 		    	System.out.println(ex.getMessage());
 		        JOptionPane.showMessageDialog(registerFrame, "Error: " + ex.getMessage());
 		        
 		    }
 		});
    }
 	
	private static void logout(JFrame menuFrame, JFrame initialFrame){
        try {
            boolean res = controller.logout(userId, token);
			if (res) {
				JOptionPane.showMessageDialog(menuFrame, "Logout Successful");
				menuFrame.dispose();
				initialFrame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(menuFrame, "Logout Failed");
			}
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(menuFrame, "Error: " + ex.getMessage());
        }
    }
    
	private static void openCreateSessionWindow() {
		JFrame createSessionFrame = new JFrame("Create Training Session");
		createSessionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createSessionFrame.setSize(400, 600);

		JPanel panel = new JPanel(new GridLayout(6, 2));


		panel.add(new JLabel("Title:"));
		JTextField titleField = new JTextField("Special Training Session");
		panel.add(titleField);

		panel.add(new JLabel("Sport:"));
		JTextField sportField = new JTextField("Football");
		panel.add(sportField);

		panel.add(new JLabel("Distance (km):"));
		JTextField distanceField = new JTextField("45");
		panel.add(distanceField);

		panel.add(new JLabel("Start Date:"));
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(new Date());
        panel.add(startDateSpinner);

		panel.add(new JLabel("Duration (minutes):"));
		JTextField durationField = new JTextField("90");
		panel.add(durationField);

		JButton createButton = new JButton("Create Session");
		panel.add(createButton);

		createSessionFrame.add(panel);
		createSessionFrame.setVisible(true);

		createButton.addActionListener(e -> {
			String title = titleField.getText();
			String sport = sportField.getText();
			String distance = distanceField.getText();
			Date startDate = (Date) startDateSpinner.getValue(); 
			String duration = durationField.getText();
			
			
            if (userId.isEmpty() || token.isEmpty() || title.isEmpty() || sport.isEmpty() || distance.isEmpty() || duration.isEmpty()) {
                JOptionPane.showMessageDialog(createSessionFrame, "Please, fill in the blank spaces", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
			try {
				 TrainingSession response = controller.session(userId, token, title, sport, Float.parseFloat(distance), startDate, Float.parseFloat(duration));

	                // Validación de la respuesta
				 if (response == null || response.sessionId() == null || 
				    response.sessionId().isEmpty() ||
					response.title() == null || response.title().isEmpty() || response.sport() == null || response.sport().isEmpty() ||
					response.distance() == null || response.distance() <= 0 || response.startDate() == null || response.duration() == null || response.duration() <= 0)  {
	                    JOptionPane.showMessageDialog(createSessionFrame, "Error: Invalid Server Response.", "Error", JOptionPane.ERROR_MESSAGE);
	                    return;
	                }

	                String sessionId = response.sessionId();

	                JOptionPane.showMessageDialog(createSessionFrame, "Session created succesfully:\nSession ID: " + sessionId);
	                createSessionFrame.dispose();
	                
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(createSessionFrame, "Error: " + ex.getMessage());
			}
		});
	}
	
	private static void querySessions() {
		JFrame createSessionsFrame = new JFrame("User Training Sessions");
		createSessionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createSessionsFrame.setSize(500, 600);
		createSessionsFrame.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());

		JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
		startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
		startDateSpinner.setValue(new Date());

		JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
		endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
		endDateSpinner.setValue(new Date());

	JButton fetchButton = new JButton("Get Sessions");
	
	topPanel.add(new JLabel("Start Date:"));
	topPanel.add(startDateSpinner);
	topPanel.add(new JLabel("End Date:"));
	topPanel.add(endDateSpinner);
	topPanel.add(fetchButton);
	DefaultListModel<String> listModel = new DefaultListModel<>();
	JList<String> sessionList = new JList<>(listModel);
	JScrollPane scrollPane = new JScrollPane(sessionList);

	createSessionsFrame.add(topPanel, BorderLayout.NORTH);
	createSessionsFrame.add(scrollPane, BorderLayout.CENTER);

	fetchButton.addActionListener(e -> {
	    listModel.clear();  // Limpiar la lista antes de actualizar
	    try {
	        Date startDate = (Date) startDateSpinner.getValue();
	        Date endDate = (Date) endDateSpinner.getValue();
	        
	        List<TrainingSession> sessions = controller.sessions(userId, token, startDate, endDate);

	        if (sessions.isEmpty()) {
	            listModel.addElement("No sessions found in this interval.");
	        } else {
	            for (TrainingSession session : sessions) {
	                String sessionInfo = String.format("%s - %s - %.2f km - %s",
	                        session.title(), session.sport(), session.distance(), session.startDate());
	                listModel.addElement(sessionInfo);
	            }
	        }
	    } catch (Exception ex) {
	        listModel.addElement("Error loading sessions: " + ex.getMessage());
	    }
	});
	createSessionsFrame.setVisible(true);
	}

	/*
	public static void querySessions() {
	    // Mostrar el marco de las sesiones del usuario
	    JFrame createSessionsFrame = new JFrame("User Training Sessions");
	    createSessionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    createSessionsFrame.setSize(500, 600);
	    createSessionsFrame.setLayout(new BorderLayout());

	    JPanel topPanel = new JPanel();
	    topPanel.setLayout(new FlowLayout());

	    // Botón para obtener las sesiones
	    JButton fetchButton = new JButton("Get Sessions");

	    // Agregar el botón al panel superior
	    topPanel.add(fetchButton);

	    // Modelo de la lista para mostrar las sesiones
	    DefaultListModel<String> listModel = new DefaultListModel<>();
	    JList<String> sessionList = new JList<>(listModel);
	    JScrollPane scrollPane = new JScrollPane(sessionList);

	    createSessionsFrame.add(topPanel, BorderLayout.NORTH);
	    createSessionsFrame.add(scrollPane, BorderLayout.CENTER);

	    // Acción cuando se presiona el botón "Get Sessions"
	    fetchButton.addActionListener(e -> {
	        listModel.clear();  // Limpiar la lista antes de actualizar
	        try {
	            // Establecer fechas por defecto
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            Date startDate = sdf.parse("2020-01-01");  // Fecha de inicio por defecto (1 de enero de 2020)
	            Date endDate = new Date();  // Fecha de finalización por defecto (la fecha actual)

	            // Llamar al controlador para obtener las sesiones dentro del rango de fechas
	            // Suponiendo que tienes un controlador con los métodos necesarios
	            List<TrainingSession> sessions = controller.sessions(userId, token, startDate, endDate);

	            // Verificar si hay sesiones y agregarlas a la lista
	            if (sessions.isEmpty()) {
	                listModel.addElement("No sessions found.");
	            } else {
	                for (TrainingSession session : sessions) {
	                    // Formatear la sesión a una cadena legible
	                    String sessionInfo = String.format("%s - %s - %.2f km - %s",
	                            session.title(), session.sport(), session.distance(), sdf.format(session.startDate()));
	                    listModel.addElement(sessionInfo);
	                }
	            }
	        } catch (Exception ex) {
	            listModel.addElement("Error loading sessions: " + ex.getMessage());
	        }
	    });

	    createSessionsFrame.setVisible(true);
	}*/


    


	private static void setupChallenge() {

		JFrame createChallengeFrame = new JFrame("Create Challenge");
		createChallengeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createChallengeFrame.setSize(400, 600);

		JPanel panel = new JPanel(new GridLayout(8, 2));

		panel.add(new JLabel("Title:"));
		JTextField titleField = new JTextField("Special Training Session");
		panel.add(titleField);

		panel.add(new JLabel("Sport:"));
		JTextField sportField = new JTextField("Football");
		panel.add(sportField);

		panel.add(new JLabel("Distance (km):"));
		JTextField distanceField = new JTextField("45");
		panel.add(distanceField);

		panel.add(new JLabel("Start Date:"));
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(new Date());
        panel.add(startDateSpinner);
		
        panel.add(new JLabel("End Date:"));
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.setValue(new Date());
        panel.add(endDateSpinner);

		panel.add(new JLabel("Target Time (minutes):"));
		JTextField targetTimeField = new JTextField("90");
		panel.add(targetTimeField);

		JButton createButton = new JButton("Set Up Challenge💪");
		panel.add(createButton);

		createChallengeFrame.add(panel);
		createChallengeFrame.setVisible(true);

		createButton.addActionListener(e -> {
			String title = titleField.getText();
			String sport = sportField.getText();
			String distance = distanceField.getText();
			Date startDate = (Date) startDateSpinner.getValue(); 
			Date endDate = (Date) endDateSpinner.getValue(); 
			String targetTime = targetTimeField.getText();
			
			
            if (userId.isEmpty() || token.isEmpty() || title.isEmpty() || sport.isEmpty() || distance.isEmpty() || targetTime.isEmpty()) {
                JOptionPane.showMessageDialog(createChallengeFrame, "Please, fill in the blank spaces", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
			try {
				 Challenge response = controller.challenge(userId, token, title, startDate, endDate, Integer.parseInt(targetTime), Float.parseFloat(distance), sport);

	                // Validación de la respuesta
				 if (response == null || response.challengeId() == null || 
				    response.challengeId().isEmpty() || response.userId() == null || response.userId().isEmpty() ||
					response.challengeName() == null || response.challengeName().isEmpty() || response.sport() == null || response.sport().isEmpty() ||
					response.targetTime() == null || response.targetTime() <= 0 || response.startDate() == null || response.targetDistance() == null || response.targetDistance() <= 0)  {
	                    JOptionPane.showMessageDialog(createChallengeFrame, "Error: Invalid Server Response.", "Error", JOptionPane.ERROR_MESSAGE);
	                    return;
	                }

	                String challengeId = response.challengeId();

	                JOptionPane.showMessageDialog(createChallengeFrame, "Challenge created succesfully:\nChallenge ID: " + challengeId);
	                createChallengeFrame.dispose();
	                
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(createChallengeFrame, "Error: " + ex.getMessage());
			}
		});
	}
	
	private static void queryChallenges() {
    }
	
	private static void addChallenge() {
		// TODO Auto-generated method stub
	}
	
	private static void challengesStatus() {
		// TODO Auto-generated method stub
	}
    
}