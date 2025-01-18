package strava.client.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;
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

                 userId = response.get("userId");
                 token = response.get("token");

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


    
    static class CustomButton extends JButton {
    	 public CustomButton(String text) {
    	     super(text);
    	     setFocusPainted(false);
    	     setContentAreaFilled(false);
    	     setBorder(BorderFactory.createLineBorder(new Color(252, 76, 2), 2));
    	     setForeground(new Color(252, 76, 2));
    	     setFont(new Font("SansSerif", Font.PLAIN, 16));
    	     setHorizontalTextPosition(JButton.CENTER);
    	     setVerticalTextPosition(JButton.BOTTOM);
    	     setCursor(new Cursor(Cursor.HAND_CURSOR));
    	 }
    	}
    private static void openAPIMenuWindow(JFrame initialFrame) {

        JFrame menuFrame = new JFrame("STRAVA MENU");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(800, 600);
        menuFrame.setResizable(false);
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Dibujar fondo degradado (blanco a naranja)
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, getWidth(), 0, new Color(252, 76, 2));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setBorder(new LineBorder(new Color(255, 69, 0), 5));

        topPanel.setPreferredSize(new Dimension(800, 100));
        topPanel.setLayout(null);

        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(10, 19, 232, 70); // Ajustar tamaño del label
        ImageIcon logoIcon = new ImageIcon(SwingClientGUI.class.getResource("strava-logo-1536x323.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(232, 70, Image.SCALE_SMOOTH); // Ajustar tamaño de la imagen
        logoLabel.setIcon(new ImageIcon(scaledImage));
        topPanel.add(logoLabel);

        JButton logOutButton = new JButton();
        logOutButton.setBounds(712, 11, 58, 58); // Posición y tamaño del botón
        ImageIcon loginIcon = new ImageIcon(SwingClientGUI.class.getResource("log_off-512.png")); // Ruta de la imagen del botón
        Image scaledLoginImage = loginIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Escalar la imagen
        logOutButton.setIcon(new ImageIcon(scaledLoginImage));
        logOutButton.setFocusPainted(false);
        logOutButton.setContentAreaFilled(false);
        logOutButton.setBorderPainted(false);
        logOutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(logOutButton);

        menuFrame.getContentPane().add(topPanel, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("LOGOUT");
        lblNewLabel.setBounds(712, 67, 76, 14);
        topPanel.add(lblNewLabel);

        // Crear el mainPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null); // Layout absoluto

        // Crear un JLabel para la imagen de fondo
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, -20, 800, 483); // Tamaño y posición del fondo
        ImageIcon backgroundIcon = new ImageIcon(SwingClientGUI.class.getResource("thumb-1920-504095.jpg"));
        Image scaledBackground = backgroundIcon.getImage().getScaledInstance(800, 500, Image.SCALE_SMOOTH); // Escalar la imagen
        backgroundLabel.setIcon(new ImageIcon(scaledBackground));

        // Crear los botones y añadirlos al panel (por encima del fondo)
        CustomButton newChallengeBtn = new CustomButton("SET UP A NEW CHALLENGE");
        newChallengeBtn.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
        newChallengeBtn.setBackground(new Color(255, 69, 0));
        newChallengeBtn.setForeground(new Color(255, 69, 0));
        newChallengeBtn.setBounds(50, 113, 352, 59);
        mainPanel.add(newChallengeBtn);

        CustomButton activeChallengesBtn = new CustomButton("ACTIVE CHALLENGES");
        activeChallengesBtn.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
        activeChallengesBtn.setForeground(new Color(255, 69, 0));
        activeChallengesBtn.setBounds(50, 244, 352, 59);
        mainPanel.add(activeChallengesBtn);

        CustomButton newSessionBtn = new CustomButton("START A NEW SESSION");
        newSessionBtn.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
        newSessionBtn.setForeground(new Color(255, 69, 0));
        newSessionBtn.setBounds(412, 113, 352, 59);
        mainPanel.add(newSessionBtn);

        CustomButton sessionsBtn = new CustomButton("MY SESSIONS");
        sessionsBtn.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
        sessionsBtn.setForeground(new Color(255, 69, 0));
        sessionsBtn.setBounds(412, 244, 352, 59);
        mainPanel.add(sessionsBtn);
        
        CustomButton statusBtn = new CustomButton("CHALLENGE STATUS");
        statusBtn.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
        statusBtn.setForeground(new Color(255, 69, 0));
        statusBtn.setBounds(230, 340, 352, 59);
        mainPanel.add(statusBtn);

        // Asegurar que la imagen de fondo esté detrás de los botones

        mainPanel.add(backgroundLabel);  // Primero añades la imagen de fondo
        mainPanel.setComponentZOrder(backgroundLabel, mainPanel.getComponentCount() - 1);
        menuFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Acciones de los botones
        logOutButton.addActionListener(e -> logout(menuFrame, initialFrame));
        newChallengeBtn.addActionListener(e -> setupChallenge());
        activeChallengesBtn.addActionListener(e -> buenqueryChallenges());
        newSessionBtn.addActionListener(e -> openCreateSessionWindow());
        sessionsBtn.addActionListener(e -> querySessions());
        statusBtn.addActionListener(e -> challengesStatus());

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private static void openRegisterWindow() {
		JFrame registerFrame = new JFrame("Register");
        registerFrame.setResizable(false);
        registerFrame.setSize(1024, 670);
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.getContentPane().setLayout(null);
        registerFrame.setLocationRelativeTo(null);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(SwingClientGUI.class.getResource("regUI.jpg")));
        lblNewLabel.setBounds(0, 0, 1024, 633);
        registerFrame.getContentPane().add(lblNewLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.DARK_GRAY);
        emailLabel.setBounds(300, 150, 200, 30);
        lblNewLabel.add(emailLabel);

        JTextField emailField = new JTextField("god@gmail.com");
        emailField.setBounds(400, 150, 200, 30);
        lblNewLabel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setBounds(300, 200, 200, 30);
        lblNewLabel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField("password123");
        passwordField.setBounds(400, 200, 200, 30);
        lblNewLabel.add(passwordField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBounds(300, 250, 200, 30);
        lblNewLabel.add(nameLabel);

        JTextField nameField = new JTextField("Godtzon");
        nameField.setBounds(400, 250, 200, 30);
        lblNewLabel.add(nameField);

        JLabel birthdateLabel = new JLabel("Birthdate (y-M-d):");
        birthdateLabel.setForeground(Color.BLACK);
        birthdateLabel.setBounds(300, 300, 200, 30);
        lblNewLabel.add(birthdateLabel);

        JTextField birthdateField = new JTextField("2004-02-29");
        birthdateField.setBounds(400, 300, 200, 30);
        lblNewLabel.add(birthdateField);

        JLabel weightLabel = new JLabel("Weight (kg):");
        weightLabel.setForeground(Color.BLACK);
        weightLabel.setBounds(300, 350, 200, 30);
        lblNewLabel.add(weightLabel);

        JTextField weightField = new JTextField("80");
        weightField.setBounds(400, 350, 200, 30);
        lblNewLabel.add(weightField);

        JLabel heightLabel = new JLabel("Height (cm):");
        heightLabel.setForeground(Color.BLACK);
        heightLabel.setBounds(300, 400, 200, 30);
        lblNewLabel.add(heightLabel);

        JTextField heightField = new JTextField("180");
        heightField.setBounds(400, 400, 200, 30);
        lblNewLabel.add(heightField);

        JLabel maxHeartRateLabel = new JLabel("Max Heart Rate:");
        maxHeartRateLabel.setForeground(Color.BLACK);
        maxHeartRateLabel.setBounds(300, 450, 200, 30);
        lblNewLabel.add(maxHeartRateLabel);

        JTextField maxHeartRateField = new JTextField("150");
        maxHeartRateField.setBounds(400, 450, 200, 30);
        lblNewLabel.add(maxHeartRateField);

        JLabel restHeartRateLabel = new JLabel("Rest Heart Rate:");
        restHeartRateLabel.setForeground(Color.BLACK);
        restHeartRateLabel.setBounds(300, 500, 200, 30);
        lblNewLabel.add(restHeartRateLabel);

        JTextField restHeartRateField = new JTextField("70");
        restHeartRateField.setBounds(400, 500, 200, 30);
        lblNewLabel.add(restHeartRateField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(425, 550, 100, 30);
        lblNewLabel.add(registerButton);

        // Set layout and visibility
        lblNewLabel.setLayout(null);
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
	    createSessionFrame.setSize(800, 600);
	    createSessionFrame.setLocationRelativeTo(null);

	    // Crear el panel superior con fondo degradado
	    JPanel topPanel = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2d = (Graphics2D) g;

	            // Dibujar fondo degradado (blanco a naranja)
	            GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, getWidth(), 0, new Color(252, 76, 2)); // De blanco a naranja
	            g2d.setPaint(gradient);
	            g2d.fillRect(0, 0, getWidth(), getHeight());
	        }
	    };
	    topPanel.setPreferredSize(new Dimension(400, 100)); // Alto del panel
	    topPanel.setLayout(new BorderLayout());

	    // Título del panel superior
	    JLabel topLabel = new JLabel("CREATE TRAINING SESSION", SwingConstants.CENTER);
	    topLabel.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 25));
	    topLabel.setForeground(Color.white);
	    topPanel.add(topLabel, BorderLayout.CENTER);

	    // Añadir el panel superior al marco
	    createSessionFrame.add(topPanel, BorderLayout.NORTH);

	    // Crear el panel principal con los campos de entrada (como en el código anterior)
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    panel.setBackground(new Color(255, 240, 220)); // Color de fondo suave (tono cálido)

	    panel.add(Box.createVerticalStrut(20)); // Espaciado entre el título y los campos

	    // Crear los campos de entrada con un panel contenedor
	    JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
	    inputPanel.setBackground(new Color(255, 240, 220)); // Fondo suave para los campos
	    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Margen interno

	    // Fuente para los JLabel
	    Font labelFont = new Font("Rockwell Extra Bold", Font.PLAIN, 19);  // Fuente para los JLabel

	    // Crear los JLabel con la nueva fuente
	    JLabel titleFieldLabel = new JLabel("Title:");
	    titleFieldLabel.setFont(labelFont);
	    titleFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField titleField = new JTextField("Special Training Session");
	    titleField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JLabel sportFieldLabel = new JLabel("Sport:");
	    sportFieldLabel.setFont(labelFont);
	    sportFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField sportField = new JTextField("Football");
	    sportField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JLabel distanceFieldLabel = new JLabel("Distance (km):");
	    distanceFieldLabel.setFont(labelFont);
	    distanceFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField distanceField = new JTextField("45");
	    distanceField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JLabel startDateFieldLabel = new JLabel("Start Date:");
	    startDateFieldLabel.setFont(labelFont);
	    startDateFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
	    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
	    startDateSpinner.setValue(new Date());

	    JLabel durationFieldLabel = new JLabel("Duration (minutes):");
	    durationFieldLabel.setFont(labelFont);
	    durationFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField durationField = new JTextField("90");
	    durationField.setFont(new Font("Arial", Font.PLAIN, 14));

	    // Añadimos los campos y etiquetas al inputPanel
	    inputPanel.add(titleFieldLabel);
	    inputPanel.add(titleField);
	    inputPanel.add(sportFieldLabel);
	    inputPanel.add(sportField);
	    inputPanel.add(distanceFieldLabel);
	    inputPanel.add(distanceField);
	    inputPanel.add(startDateFieldLabel);
	    inputPanel.add(startDateSpinner);
	    inputPanel.add(durationFieldLabel);
	    inputPanel.add(durationField);

	    panel.add(inputPanel);
	    panel.add(Box.createVerticalStrut(20)); // Espaciado entre los campos y el botón

	    // Botón de creación de sesión
	    CustomButton createButton = new CustomButton("CREATE SESSION");
	    createButton.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
	    createButton.setBackground(new Color(255, 69, 0)); // Naranja vibrante
	    createButton.setFocusPainted(false);
	    createButton.setPreferredSize(new Dimension(220, 40));
	    createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Centra los componentes en el panel
	    buttonPanel.setBackground(new Color(255, 240, 220)); // Fondo suave (opcional)

	    // Añadir el botón al panel
	    buttonPanel.add(createButton);

	    // Añadir el panel al panel principal
	    panel.add(buttonPanel);

	    // Crear el frame
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

	            JOptionPane.showMessageDialog(createSessionFrame, "Session created successfully:\nSession ID: " + sessionId);
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
	    createChallengeFrame.setSize(800, 600);
	    createChallengeFrame.setLocationRelativeTo(null);

	    // Crear el panel superior con fondo degradado
	    JPanel topPanel = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2d = (Graphics2D) g;

	            // Dibujar fondo degradado (blanco a naranja)
	            GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, getWidth(), 0, new Color(252, 76, 2)); // De blanco a naranja
	            g2d.setPaint(gradient);
	            g2d.fillRect(0, 0, getWidth(), getHeight());
	        }
	    };
	    topPanel.setPreferredSize(new Dimension(800, 100)); // Alto del panel
	    topPanel.setLayout(new BorderLayout());

	    // Título del panel superior
	    JLabel topLabel = new JLabel("CREATE CHALLENGE", SwingConstants.CENTER);
	    topLabel.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 25));
	    topLabel.setForeground(Color.white);
	    topPanel.add(topLabel, BorderLayout.CENTER);

	    // Añadir el panel superior al marco
	    createChallengeFrame.add(topPanel, BorderLayout.NORTH);

	    // Crear el panel principal con los campos de entrada (como en el código anterior)
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    panel.setBackground(new Color(255, 240, 220)); // Color de fondo suave (tono cálido)

	    panel.add(Box.createVerticalStrut(20)); // Espaciado entre el título y los campos

	    // Crear los campos de entrada con un panel contenedor
	    JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
	    inputPanel.setBackground(new Color(255, 240, 220)); // Fondo suave para los campos
	    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Margen interno

	    // Fuente para los JLabel
	    Font labelFont = new Font("Rockwell Extra Bold", Font.PLAIN, 19);  // Fuente para los JLabel

	    // Crear los JLabel con la nueva fuente
	    JLabel titleFieldLabel = new JLabel("Title:");
	    titleFieldLabel.setFont(labelFont);
	    titleFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField titleField = new JTextField("Special Challenge");
	    titleField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JLabel sportFieldLabel = new JLabel("Sport:");
	    sportFieldLabel.setFont(labelFont);
	    sportFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField sportField = new JTextField("Football");
	    sportField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JLabel distanceFieldLabel = new JLabel("Distance (km):");
	    distanceFieldLabel.setFont(labelFont);
	    distanceFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField distanceField = new JTextField("45");
	    distanceField.setFont(new Font("Arial", Font.PLAIN, 14));

	    JLabel startDateFieldLabel = new JLabel("Start Date:");
	    startDateFieldLabel.setFont(labelFont);
	    startDateFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
	    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
	    startDateSpinner.setValue(new Date());

	    JLabel endDateFieldLabel = new JLabel("End Date:");
	    endDateFieldLabel.setFont(labelFont);
	    endDateFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
	    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
	    endDateSpinner.setValue(new Date());

	    JLabel targetTimeFieldLabel = new JLabel("Target Time (minutes):");
	    targetTimeFieldLabel.setFont(labelFont);
	    targetTimeFieldLabel.setForeground(new Color(252, 76, 2)); // Color naranja
	    JTextField targetTimeField = new JTextField("90");
	    targetTimeField.setFont(new Font("Arial", Font.PLAIN, 14));

	    // Añadimos los campos y etiquetas al inputPanel
	    inputPanel.add(titleFieldLabel);
	    inputPanel.add(titleField);
	    inputPanel.add(sportFieldLabel);
	    inputPanel.add(sportField);
	    inputPanel.add(distanceFieldLabel);
	    inputPanel.add(distanceField);
	    inputPanel.add(startDateFieldLabel);
	    inputPanel.add(startDateSpinner);
	    inputPanel.add(endDateFieldLabel);
	    inputPanel.add(endDateSpinner);
	    inputPanel.add(targetTimeFieldLabel);
	    inputPanel.add(targetTimeField);

	    panel.add(inputPanel);
	    panel.add(Box.createVerticalStrut(20)); // Espaciado entre los campos y el botón

	    // Botón de creación de desafío
	    CustomButton createButton = new CustomButton("CREATE SESSION");
	    createButton.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 19));
	    createButton.setBackground(new Color(255, 69, 0)); // Naranja vibrante
	    createButton.setFocusPainted(false);
	    createButton.setPreferredSize(new Dimension(220, 40));
	    createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Centra los componentes en el panel
	    buttonPanel.setBackground(new Color(255, 240, 220)); // Fondo suave (opcional)

	    // Añadir el botón al panel
	    buttonPanel.add(createButton);

	    // Añadir el panel al panel principal
	    panel.add(buttonPanel);

	    // Crear el frame
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

	            JOptionPane.showMessageDialog(createChallengeFrame, "Challenge created successfully:\nChallenge ID: " + challengeId);
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
	    createChallengesFrame.setSize(800, 600);
	    createChallengesFrame.setResizable(false);
	    createChallengesFrame.setLayout(new BorderLayout());
	    createChallengesFrame.setLocationRelativeTo(null);

	    // Panel superior con el gradiente y título
	    JPanel topPanel = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2d = (Graphics2D) g;

	            GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, getWidth(), 0, new Color(252, 76, 2));
	            g2d.setPaint(gradient);
	            g2d.fillRect(0, 0, getWidth(), getHeight());
	        }
	    };
	    topPanel.setPreferredSize(new Dimension(800, 100));
	    topPanel.setLayout(new BorderLayout());

	    JLabel topLabel = new JLabel("CHALLENGES", SwingConstants.CENTER);
	    topLabel.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 25));
	    topLabel.setForeground(Color.WHITE);
	    topPanel.add(topLabel, BorderLayout.CENTER);
	    createChallengesFrame.add(topPanel, BorderLayout.NORTH);

	    // Panel para los controles de búsqueda
	    JPanel controlsPanel = new JPanel();
	    controlsPanel.setLayout(new GridBagLayout());

	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(5, 5, 5, 5);

	    JLabel startDateLabel = new JLabel("Start Date:");
	    JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
	    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
	    startDateSpinner.setValue(new Date());

	    JLabel endDateLabel = new JLabel("End Date:");
	    JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
	    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
	    endDateSpinner.setValue(new Date());

	    JLabel sportLabel = new JLabel("Sport:");
	    JTextField sportField = new JTextField();

	    JButton fetchButton = new JButton("Get Challenges");

	    // Colocar los componentes en la primera fila
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    controlsPanel.add(startDateLabel, gbc);

	    gbc.gridx = 1;
	    controlsPanel.add(startDateSpinner, gbc);

	    gbc.gridx = 2;
	    controlsPanel.add(endDateLabel, gbc);

	    gbc.gridx = 3;
	    controlsPanel.add(endDateSpinner, gbc);

	    gbc.gridx = 4;
	    controlsPanel.add(sportLabel, gbc);

	    gbc.gridx = 5;
	    gbc.weightx = 1.0;
	    controlsPanel.add(sportField, gbc);

	    // Colocar el botón en la segunda fila
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 6; // El botón ocupa toda la fila
	    gbc.weightx = 0;
	    controlsPanel.add(fetchButton, gbc);

	    createChallengesFrame.add(controlsPanel, BorderLayout.CENTER);

	    // Lista para mostrar las sesiones
	    DefaultListModel<Challenge> listModel = new DefaultListModel<>();
	    JList<Challenge> sessionList = new JList<>(listModel);
	    JScrollPane scrollPane = new JScrollPane(sessionList);

	    // Configuración del renderizado personalizado para la lista
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

	    createChallengesFrame.add(scrollPane, BorderLayout.SOUTH);

	    fetchButton.addActionListener(e -> {
	        listModel.clear();
	        try {
	            Date startDate = (Date) startDateSpinner.getValue();
	            Date endDate = (Date) endDateSpinner.getValue();
	            String sport = sportField.getText();

	            List<Challenge> challenges = controller.challenges(startDate, endDate, sport);

	            if (challenges.isEmpty()) {
	                listModel.addElement(null);
	            } else {
	                for (Challenge challenge : challenges) {
	                    listModel.addElement(challenge);
	                }
	            }
	        } catch (Exception ex) {
	            listModel.addElement(null);
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
	                            StringBuilder result = new StringBuilder("Participation registered!\n");
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
	
	private static void buenqueryChallenges() {
	    JFrame createChallengesFrame = new JFrame("Search Challenges");
	    createChallengesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    createChallengesFrame.setSize(800, 600);
	    createChallengesFrame.setResizable(false);
	    createChallengesFrame.setLocationRelativeTo(null);
	    createChallengesFrame.setLayout(new BorderLayout());

	    // Panel superior con el gradiente y título
	    JPanel topPanel = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2d = (Graphics2D) g;
	            GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, getWidth(), 0, new Color(252, 76, 2));
	            g2d.setPaint(gradient);
	            g2d.fillRect(0, 0, getWidth(), getHeight());
	        }
	    };
	    topPanel.setPreferredSize(new Dimension(800, 100));
	    topPanel.setLayout(new BorderLayout());

	    JLabel topLabel = new JLabel("CHALLENGES", SwingConstants.CENTER);
	    topLabel.setFont(new Font("Rockwell Extra Bold", Font.PLAIN, 25));
	    topLabel.setForeground(Color.WHITE);
	    topPanel.add(topLabel, BorderLayout.CENTER);
	    createChallengesFrame.add(topPanel, BorderLayout.NORTH);

	    // Panel para los controles de búsqueda
	    JPanel controlsPanel = new JPanel();
	    controlsPanel.setLayout(new GridBagLayout());
	    controlsPanel.setBackground(new Color(255, 240, 220));

	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(10, 10, 10, 10);

	    JLabel startDateLabel = new JLabel("Start Date:");
	    JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
	    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
	    startDateSpinner.setValue(new Date());

	    JLabel endDateLabel = new JLabel("End Date:");
	    JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
	    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
	    endDateSpinner.setValue(new Date());

	    JLabel sportLabel = new JLabel("Sport:");
	    JTextField sportField = new JTextField();

	    JButton fetchButton = new JButton("Get Challenges");

	    // Colocar componentes en el panel de controles
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    controlsPanel.add(startDateLabel, gbc);

	    gbc.gridx = 1;
	    controlsPanel.add(startDateSpinner, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    controlsPanel.add(endDateLabel, gbc);

	    gbc.gridx = 1;
	    controlsPanel.add(endDateSpinner, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    controlsPanel.add(sportLabel, gbc);

	    gbc.gridx = 1;
	    controlsPanel.add(sportField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    controlsPanel.add(fetchButton, gbc);

	    createChallengesFrame.add(controlsPanel, BorderLayout.CENTER);

	    // Lista para mostrar los resultados
	    DefaultListModel<Challenge> listModel = new DefaultListModel<>();
	    JList<Challenge> sessionList = new JList<>(listModel);
	    JScrollPane scrollPane = new JScrollPane(sessionList);
	    sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    sessionList.setBackground(new Color(255, 240, 220));
	    createChallengesFrame.add(scrollPane, BorderLayout.SOUTH);

	    // Render personalizado para los elementos de la lista
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

	    // Acción del botón "Get Challenges"
	    fetchButton.addActionListener(e -> {
	        listModel.clear();
	        try {
	            Date startDate = (Date) startDateSpinner.getValue();
	            Date endDate = (Date) endDateSpinner.getValue();
	            String sport = sportField.getText();

	            List<Challenge> challenges = controller.challenges(startDate, endDate, sport);

	            if (challenges.isEmpty()) {
	                JOptionPane.showMessageDialog(createChallengesFrame, "No challenges found.", "Info", JOptionPane.INFORMATION_MESSAGE);
	            } else {
	                for (Challenge challenge : challenges) {
	                    listModel.addElement(challenge);
	                }
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(createChallengesFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  

	    JScrollPane scrollPane = new JScrollPane(panel);
	    challengeStatusFrame.add(scrollPane);

	    try {
	        Map<String, Integer> statusMap = controller.challengeStatus(userId, token);

	        if (statusMap.isEmpty()) {
	            JLabel noChallengesLabel = new JLabel("No challenges with progress found.");
	            panel.add(noChallengesLabel);
	        } else {
	            for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
	                JPanel challengePanel = new JPanel();
	                challengePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));  

	                JLabel challengeLabel = new JLabel(entry.getKey());
	                challengePanel.add(challengeLabel);

	                JProgressBar progressBar = new JProgressBar(0, 100);
	                progressBar.setValue(entry.getValue());  
	                progressBar.setStringPainted(true); 
	                progressBar.setPreferredSize(new Dimension(150, 20)); 
	                challengePanel.add(progressBar);

	                panel.add(challengePanel);
	            }
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        JLabel errorLabel = new JLabel("Error: " + ex.getMessage());
	        panel.add(errorLabel);
	    }

	    challengeStatusFrame.setVisible(true);
	}
    
}