
package main.java.when2meet.gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class JoinEventFrame extends JFrame {
    private static JoinEventFrame instance;

    // Constructor
    public JoinEventFrame() {
        setTitle("When2Meet Client: Join Event");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initMenu();
        initUI();
        setVisible(true);
        logMessage("JoinEventFrame created.");
    }

    // Create menu bar
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        
        // Menu item to return to the main frame
        JMenuItem returnItem = new JMenuItem("Main Menu");
        returnItem.addActionListener((ActionEvent e) -> {
            dispose();
            MainFrame.getInstance();
        });
        menu.add(returnItem);
        
        // Menu item to exit the application
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        menu.add(exitItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    // Create UI
    private void initUI() {
        setLayout(new BorderLayout());
        
        // Title at the top
        JLabel titleLabel = new JLabel("Event Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
    
        // Login Panel right below the title
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        loginPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(10);
        loginPanel.add(nameField);
        loginPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField(10);
        loginPanel.add(passwordField);
        JButton signInButton = new JButton("Register / Sign In");
        loginPanel.add(signInButton);
        add(loginPanel, BorderLayout.NORTH);
        
        // Split pane for My Availability and Group Availability
        JPanel myAvailPanel = createAvailabilityPanel("My Availability");
        JPanel grpAvailPanel = createAvailabilityPanel("Group Availability");
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                              myAvailPanel,
                                              grpAvailPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
        
        // Refresh button at the bottom
        JButton refreshButton = new JButton("Refresh Availability");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Helper functions for creating sample availability panels
    private JPanel createAvailabilityPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title, JLabel.CENTER), BorderLayout.NORTH);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(11, 3));
        mainPanel.add(new JLabel("Column 1", JLabel.CENTER));
        mainPanel.add(new JLabel("Column 2", JLabel.CENTER));
        mainPanel.add(new JLabel("Column 3", JLabel.CENTER));
        for (int i = 9; i < 19; i++) {
            for (int j = 0; j < 3; j++) {
                JPanel hourPanel = new JPanel();
                hourPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
                hourPanel.setBackground(Color.lightGray);
                hourPanel.setToolTipText(i + ":00 - " + (i + 1) + ":00");
                hourPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hourPanel.setBackground(Color.darkGray);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hourPanel.setBackground(Color.lightGray);
                    }
                });
                mainPanel.add(hourPanel);
            }
        }
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }    
    
    // Helper function for logging
    private void logMessage(String msg) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss] ");
        String formattedTime = currentTime.format(formatter);
        System.out.println(formattedTime + msg);
    }

    // Make sure only one such frame can be created
    public static JoinEventFrame getInstance() {
        if (instance == null || !instance.isDisplayable()) {
            instance = new JoinEventFrame();
        }
        return instance;
    }
}