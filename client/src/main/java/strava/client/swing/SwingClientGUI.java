package strava.client.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.Map;
public class SwingClientGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	private static SwingClientController controller = new SwingClientController();
    private static String userId;
    private static String token;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame initialFrame = ventanaLogin();
            initialFrame.setVisible(true);
        });
    }

    
    private static JFrame ventanaLogin() {
    	JFrame frame = new JFrame("Login/Register");
         frame.setResizable(false);
         frame.setSize(1024, 670);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.getContentPane().setLayout(null);
         frame.setLocationRelativeTo(null);

         JLabel lblNewLabel = new JLabel("");
         lblNewLabel.setIcon(new ImageIcon(SwingClientGUI.class.getResource("strava-digital-1024x633.jpg")));
         lblNewLabel.setBounds(0, 0, 1024, 633);
         frame.getContentPane().add(lblNewLabel);

         JLabel emailLabel = new JLabel("Email:");
         emailLabel.setForeground(Color.WHITE);
         emailLabel.setBounds(412, 395, 200, 30);

         JTextField emailField = new JTextField("user1@example.com");
         emailField.setBounds(412, 420, 200, 30);

         JLabel passwordLabel = new JLabel("Password:");
         passwordLabel.setForeground(Color.WHITE);
         passwordLabel.setBounds(412, 445, 200, 30);

         JPasswordField passwordField = new JPasswordField("password123");
         passwordField.setBounds(412, 470, 200, 30);

         String[] providers = {"Google", "Facebook"};
         JComboBox<String> comboBox = new JComboBox<>(providers);
         comboBox.setBounds(412, 510, 200, 30);
         
         JButton loginButton = new JButton("Login");
         loginButton.setBounds(412, 550, 90, 30);

         JButton registerButton = new JButton("Register");
         registerButton.setBounds(522, 550, 90, 30);

         lblNewLabel.add(emailLabel);
         lblNewLabel.add(emailField);
         lblNewLabel.add(passwordLabel);
         lblNewLabel.add(passwordField);
         lblNewLabel.add(comboBox);
         lblNewLabel.add(loginButton);
         lblNewLabel.add(registerButton);
         lblNewLabel.setLayout(null);
         
         
         loginButton.addActionListener(e -> {
             String email = emailField.getText();
             String password = new String(passwordField.getPassword());
             String externalProv = comboBox.getSelectedItem().toString();
             
             if (email.isEmpty() || password.isEmpty() || externalProv.isEmpty()) {
                 JOptionPane.showMessageDialog(frame, "Please, fill in the blank spaces", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             try {
                 Credentials credentials = new Credentials(email, password, externalProv);
                 var response = controller.login(credentials);

                 // Validación de la respuesta
                 if (response == null || !response.containsKey("userId") || !response.containsKey("token")) {
                     JOptionPane.showMessageDialog(frame, "Error: Invalid Server Response.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                 String userId = response.get("userId");
                 String token = response.get("token");

                 JOptionPane.showMessageDialog(frame, "Login Successful:\nUser ID: " + userId + ", Token: " + token);
                 openAPIMenuWindow(frame);
                 frame.setVisible(false);
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             }
         });

         registerButton.addActionListener(e -> openRegisterWindow());
         
         return frame;
    }

    private static void openLoginWindow(JFrame initialFrame) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(400, 300);

        // Usar GridBagLayout para los componentes
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Asegura que los componentes se estiren horizontalmente
        gbc.insets = new Insets(5, 5, 5, 5);  // Espaciado entre componentes

        // Componentes de la interfaz
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField("user1@example.com");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  // El label y el campo de texto ocuparán dos columnas
        loginPanel.add(emailLabel, gbc);
        gbc.gridy = 1;
        loginPanel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField("password123");
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        loginPanel.add(passwordField, gbc);

        JLabel providerLabel = new JLabel("External Provider:");
        gbc.gridy = 4;
        loginPanel.add(providerLabel, gbc);

        String[] providers = { "Google", "Facebook" };
        JComboBox<String> providerComboBox = new JComboBox<>(providers);
        gbc.gridy = 5;
        loginPanel.add(providerComboBox, gbc);

        // Centrar y hacer que el botón ocupe toda la fila
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;  // El botón ocupará las dos columnas
        gbc.weightx = 1.0;  // Hace que el botón ocupe todo el espacio horizontal disponible
        gbc.weighty = 1.0;  // Hace que el botón ocupe todo el espacio vertical disponible
        gbc.anchor = GridBagConstraints.CENTER;  // Centra el botón
        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton, gbc);

        // Ajustar constraints de la ventana
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

        JFrame menuFrame = new JFrame("API Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 400);
        menuFrame.setLocationRelativeTo(null);

        JPanel menuPanel = new JPanel(new GridLayout(6, 1));

        JButton logoutButton = new JButton("Logout");
        JButton createSessionButton = new JButton("Create a training session");
        JButton querySessionsButton = new JButton("Query training sessions");
        JButton setupChallengeButton = new JButton("Set up a challenge");
        JButton queryChallengesButton = new JButton("Challenges");
        JButton challengeStatusButton = new JButton("Challenges status");

        menuPanel.add(logoutButton);
        menuPanel.add(createSessionButton);
        menuPanel.add(querySessionsButton);
        menuPanel.add(setupChallengeButton);
        menuPanel.add(queryChallengesButton);
        menuPanel.add(challengeStatusButton);

        menuFrame.add(menuPanel);
        menuFrame.setVisible(true);

        logoutButton.addActionListener(e -> logout(menuFrame, initialFrame));
        createSessionButton.addActionListener(e -> openCreateSessionWindow());
		querySessionsButton.addActionListener(e -> querySessions());
		setupChallengeButton.addActionListener(e -> setupChallenge());
		queryChallengesButton.addActionListener(e -> queryChallenges());
		challengeStatusButton.addActionListener(e -> challengesStatus());
       
    }
    

	private static void openRegisterWindow() {
 		JFrame registerFrame = new JFrame("Register");
 		registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 		registerFrame.setSize(400, 600);
 		registerFrame.setLocationRelativeTo(null);

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
		createSessionFrame.setLocationRelativeTo(null);

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
	    createSessionsFrame.setSize(500, 300);
	    createSessionsFrame.setLayout(new BorderLayout());
	    createSessionsFrame.setLocationRelativeTo(null);

	    JPanel topPanel = new JPanel();
	    topPanel.setLayout(new GridBagLayout());

	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(5, 5, 5, 5);

	    // Componentes
	    JLabel startDateLabel = new JLabel("Start Date:");
	    JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
	    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
	    startDateSpinner.setValue(new Date());

	    JLabel endDateLabel = new JLabel("End Date:");
	    JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
	    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
	    endDateSpinner.setValue(new Date());

	    JButton fetchButton = new JButton("Get Sessions");

	    // Añadir componentes al topPanel usando GridBagConstraints
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    topPanel.add(startDateLabel, gbc);

	    gbc.gridx = 1;
	    topPanel.add(startDateSpinner, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    topPanel.add(endDateLabel, gbc);

	    gbc.gridx = 1;
	    topPanel.add(endDateSpinner, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 2;  // El botón debe ocupar las dos columnas
	    topPanel.add(fetchButton, gbc);

	    // Lista para mostrar las sesiones
	    DefaultListModel<TrainingSession> listModel = new DefaultListModel<>();
	    JList<TrainingSession> sessionList = new JList<>(listModel);
	    JScrollPane scrollPane = new JScrollPane(sessionList);

	    // Personalizar el renderizado de la lista (solo mostrar información específica)
	    sessionList.setCellRenderer(new ListCellRenderer<TrainingSession>() {
	        @Override
	        public Component getListCellRendererComponent(JList<? extends TrainingSession> list, TrainingSession value, int index, boolean isSelected, boolean cellHasFocus) {
	            JLabel label = new JLabel();
	            if (value != null) {
	                label.setText(String.format("%s - %s - %.2f km - %s", 
	                        value.title(), value.sport(), value.distance(), value.startDate()));
	            }
	            label.setOpaque(true);
	            if (isSelected) {
	                label.setBackground(list.getSelectionBackground());
	                label.setForeground(list.getSelectionForeground());
	            } else {
	                label.setBackground(list.getBackground());
	                label.setForeground(list.getForeground());
	            }
	            return label;
	        }
	    });

	    // Añadir panels al frame
	    createSessionsFrame.add(topPanel, BorderLayout.NORTH);
	    createSessionsFrame.add(scrollPane, BorderLayout.CENTER);

	    fetchButton.addActionListener(e -> {
	        listModel.clear();  // Limpiar la lista antes de actualizar
	        try {
	            Date startDate = (Date) startDateSpinner.getValue();
	            Date endDate = (Date) endDateSpinner.getValue();

	            List<TrainingSession> sessions = controller.sessions(userId, token, startDate, endDate);

	            if (sessions.isEmpty()) {
	                listModel.addElement(null);  // No sessions found
	            } else {
	                for (TrainingSession session : sessions) {
	                    listModel.addElement(session); // Añadir el objeto TrainingSession
	                }
	            }
	        } catch (Exception ex) {
	            listModel.addElement(null);  // Error loading sessions
	        }
	    });

	    // Agregar MouseListener para copiar el sessionId al portapapeles
	    sessionList.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            int index = sessionList.locationToIndex(e.getPoint());
	            if (index != -1) {
	                TrainingSession selectedSession = listModel.getElementAt(index);
	                if (selectedSession != null) {
	                    String sessionId = selectedSession.sessionId();  // Obtener el sessionId
	                    copySessionToClipboard(sessionId);
	                }
	            }
	        }
	    });

	    createSessionsFrame.setVisible(true);
	}

	private static void copySessionToClipboard(String sessionId) {
	    StringSelection stringSelection = new StringSelection(sessionId);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, null);
	}
    


	private static void setupChallenge() {

		JFrame createChallengeFrame = new JFrame("Create Challenge");
		createChallengeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		createChallengeFrame.setSize(400, 600);
		createChallengeFrame.setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridLayout(8, 2));

		panel.add(new JLabel("Title:"));
		JTextField titleField = new JTextField("Special Challenge");
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
	    JFrame createChallengesFrame = new JFrame("Search Challenges");
	    createChallengesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    createChallengesFrame.setSize(500, 300);
	    createChallengesFrame.setLayout(new BorderLayout());
	    createChallengesFrame.setLocationRelativeTo(null);

	    JPanel topPanel = new JPanel();
	    topPanel.setLayout(new GridBagLayout());

	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(5, 5, 5, 5);

	    // Componentes
	    JLabel startDateLabel = new JLabel("Start Date:");
	    JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
	    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
	    startDateSpinner.setValue(new Date());

	    JLabel endDateLabel = new JLabel("End Date:");
	    JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
	    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
	    endDateSpinner.setValue(new Date());

	    JLabel sportLabel = new JLabel("Sport:");
	    JTextField sportField = new JTextField("Running");

	    JButton fetchButton = new JButton("Get Challenges");

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    topPanel.add(startDateLabel, gbc);

	    gbc.gridx = 1;
	    topPanel.add(startDateSpinner, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    topPanel.add(endDateLabel, gbc);

	    gbc.gridx = 1;
	    topPanel.add(endDateSpinner, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    topPanel.add(sportLabel, gbc);

	    gbc.gridx = 1;
	    topPanel.add(sportField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;  // El botón debe ocupar las dos columnas
	    topPanel.add(fetchButton, gbc);

	    // Lista para mostrar las sesiones
	    DefaultListModel<Challenge> listModel = new DefaultListModel<>();
	    JList<Challenge> sessionList = new JList<>(listModel);
	    JScrollPane scrollPane = new JScrollPane(sessionList);

	    // Personalizar el renderizado de la lista (solo mostrar información específica)
	    sessionList.setCellRenderer(new ListCellRenderer<Challenge>() {
	        @Override
	        public Component getListCellRendererComponent(JList<? extends Challenge> list, Challenge value, int index, boolean isSelected, boolean cellHasFocus) {
	            JLabel label = new JLabel();
	            if (value != null) {
	                label.setText(String.format("%s - %s - %.2f km - %s", 
	                        value.challengeName(), value.sport(), value.targetDistance(), value.startDate()));
	            }
	            label.setOpaque(true);
	            if (isSelected) {
	                label.setBackground(list.getSelectionBackground());
	                label.setForeground(list.getSelectionForeground());
	            } else {
	                label.setBackground(list.getBackground());
	                label.setForeground(list.getForeground());
	            }
	            return label;
	        }
	    });

	    // Añadir panels al frame
	    createChallengesFrame.add(topPanel, BorderLayout.NORTH);
	    createChallengesFrame.add(scrollPane, BorderLayout.CENTER);

	    fetchButton.addActionListener(e -> {
	        listModel.clear();  // Limpiar la lista antes de actualizar
	        try {
	            Date startDate = (Date) startDateSpinner.getValue();
	            Date endDate = (Date) endDateSpinner.getValue();
	            String sport = sportField.getText();
	            
	            List<Challenge> challenges = controller.challenges(startDate, endDate, sport);

	            if (challenges.isEmpty()) {
	                listModel.addElement(null);  // No challenges found
	            } else {
	                for (Challenge challenge : challenges) {
	                    listModel.addElement(challenge); // Añadir el objeto Challenge
	                }
	            }
	        } catch (Exception ex) {
	            listModel.addElement(null);  // Error loading challenges
	        }
	    });

	    
	    sessionList.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            int index = sessionList.locationToIndex(e.getPoint());
	            if (index != -1) {
	                Challenge selectedChallenge = listModel.getElementAt(index);
	                if (selectedChallenge != null) {
	                    String challengeId = selectedChallenge.challengeId();
	                    
	                    try {
	        	            List<Challenge> challenges = controller.challengeParticipant(challengeId, userId, token);

	        	            if (challenges.isEmpty()) {
	        	                JOptionPane.showMessageDialog(createChallengesFrame, "Challenge not found.", "Info", JOptionPane.INFORMATION_MESSAGE);
	        	            } else {
	        	                StringBuilder result = new StringBuilder("Participation registered:\n");
	        	                for (Challenge challenge : challenges) {
	        	                    result.append("ID: ").append(challenge.challengeId())
	        	                          .append(", Name: ").append(challenge.challengeName())
	        	                          .append(", Sport: ").append(challenge.sport())
	        	                          .append("\n");
	        	                }
	        	                JOptionPane.showMessageDialog(createChallengesFrame, result.toString(), "Challenges", JOptionPane.INFORMATION_MESSAGE);
	        	            }
	        	        } catch (Exception ex) {
	        	            ex.printStackTrace();
	        	            JOptionPane.showMessageDialog(createChallengesFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        	        }
	                }
	            }
	        }
	    });

	    createChallengesFrame.setVisible(true);
	}


    

	
	
	private static void challengesStatus() {
	    JFrame challengeStatusFrame = new JFrame("Challenge Status");
	    challengeStatusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    challengeStatusFrame.setSize(400, 300);
	    challengeStatusFrame.setLocationRelativeTo(null);

	    JPanel panel = new JPanel(new GridLayout(4, 2));

	    // Input fields
	    panel.add(new JLabel("User ID:"));
	    JTextField userIdField = new JTextField(userId);
	    panel.add(userIdField);

	    panel.add(new JLabel("Token:"));
	    JTextField tokenField = new JTextField(token);
	    panel.add(tokenField);

	    JButton fetchStatusButton = new JButton("Fetch Challenge Status");
	    panel.add(fetchStatusButton);

	    challengeStatusFrame.add(panel);
	    challengeStatusFrame.setVisible(true);

	    fetchStatusButton.addActionListener(e -> {
	        String userId = userIdField.getText();
	        String token = tokenField.getText();

	        if (userId.isEmpty() || token.isEmpty()) {
	            JOptionPane.showMessageDialog(challengeStatusFrame, "Please, fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        try {
	            Map<String, Float> statusMap = controller.challengeStatus(userId, token);

	            if (statusMap.isEmpty()) {
	                JOptionPane.showMessageDialog(challengeStatusFrame, "No challenges with progress found.", "Info", JOptionPane.INFORMATION_MESSAGE);
	            } else {
	                StringBuilder result = new StringBuilder("Challenge Progress:\n");
	                for (Map.Entry<String, Float> entry : statusMap.entrySet()) {
	                    result.append("Challenge: ").append(entry.getKey())
	                          .append(", Progress: ").append(entry.getValue()).append("%\n");
	                }
	                JOptionPane.showMessageDialog(challengeStatusFrame, result.toString(), "Challenge Status", JOptionPane.INFORMATION_MESSAGE);
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(challengeStatusFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });
	}
    
}