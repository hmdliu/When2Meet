
package main.java.when2meet.gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static MainFrame instance;

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
        JTextField eventIdField = new JTextField(8);
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
        joinEventButton.addActionListener((ActionEvent e) -> {
            dispose();
            logMessage("Invoking JoinEventFrame.");
            JoinEventFrame.getInstance();
        });
        buttonPanel.add(createEventButton);
        buttonPanel.add(joinEventButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
