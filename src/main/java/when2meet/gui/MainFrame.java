
package when2meet.gui;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;

import when2meet.gui.mapper.Event;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static MainFrame instance;
    private JTextField eventIdField;
    private static final String HOST = "http://localhost:8080/";
    private static final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(3))
        .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor
    public MainFrame() {
        setTitle("When2Meet Client: Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        pack();
        setLocationRelativeTo(null);
        setSize(400, 250);
        setVisible(true);
        logMessage("MainFrame created.");
    }
    
    // Helper function for logging
    private void logMessage(String msg) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss] ");
        String formattedTime = currentTime.format(formatter);
        System.out.println(formattedTime + msg);
    }

    // Load main frame UI
    private void initUI() {
        // Layout setup
        setLayout(new BorderLayout(5, 5));
        
        // Introductory text
        JTextArea introText = new JTextArea(
            "Welcome to When2Meet! \n\n" +
            "Create or join an event to coordinate meeting times easily. \n\n" +
            "This is a JAVA + Spring Boot replication of the original app " +
            "(https://www.when2meet.com/). Please contact the author via " +
            "haoming.liu@nyu.edu if there are any issues. :-)"
        );
        introText.setWrapStyleWord(true);
        introText.setLineWrap(true);
        introText.setEditable(false);
        introText.setOpaque(true);
        introText.setBackground(Color.WHITE);
        introText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        introText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(introText, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        eventIdField = new JTextField(8);
        inputPanel.add(new JLabel("Event ID (if joining):"));
        inputPanel.add(eventIdField);
        add(inputPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton createEventButton = new JButton("Create Event");
        JButton joinEventButton = new JButton("Join Event");
        createEventButton.addActionListener((ActionEvent e) -> {
            dispose();
            logMessage("Invoking CreateEventFrame.");
            CreateEventFrame.getInstance();
        });
        joinEventButton.addActionListener(e -> joinEvent());
        buttonPanel.add(createEventButton);
        buttonPanel.add(joinEventButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Join event by eventID
    private void joinEvent() {
        if (eventIdField.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, 
                "The event ID field is blank.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(HOST + "events/" + eventIdField.getText()))
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logMessage("Received get event response: " + response.body());
            if (response.statusCode() == 200) {
                Event event = objectMapper.readValue(response.body(), Event.class);
                dispose();
                logMessage("Invoking JoinEventFrame.");
                JoinEventFrame.getInstance(event);
            } else if (response.statusCode() == 404) {
                JOptionPane.showMessageDialog(this, 
                    "The event ID does not exist.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Unknown Error: " + response.body(),
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Make sure only one such frame can be created
    public static MainFrame getInstance() {
        if (instance == null || !instance.isDisplayable()) {
            instance = new MainFrame();
        }
        return instance;
    }

    // Application entry
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            getInstance();
        });
    }
}
