package strava.client.swing;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import strava.client.data.Credentials;
import strava.client.data.User;

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

        logoutButton.addActionListener(e -> {
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
        });

        // Implementar los otros botones con funciones similares, delegando en controller
    }

 // Método para abrir la ventana de Register
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
    
    
    
}