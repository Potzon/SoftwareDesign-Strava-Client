package strava.client.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import strava.client.data.Credentials;

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

            try {
                Credentials credentials = new Credentials(email, password, externalProv);
                var response = controller.login(credentials);
                userId = response.get("userId");
                token = response.get("token");
                JOptionPane.showMessageDialog(loginFrame, "Login Successful:\nUser ID: " + userId + ", Token: " + token);
                loginFrame.dispose();
                openAPIMenuWindow(initialFrame);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginFrame, "Error: " + ex.getMessage());
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

    private static void openRegisterWindow() {
        // Implementar lógica de registro si es necesario
        JOptionPane.showMessageDialog(null, "Register functionality not implemented yet.");
    }
    
    
    
}