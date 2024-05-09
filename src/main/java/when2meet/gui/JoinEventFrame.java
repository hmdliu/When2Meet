
package when2meet.gui;

import java.net.URI;
import java.net.http.*;
import java.net.URLEncoder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;

import when2meet.gui.mapper.Event;
import when2meet.gui.mapper.UserAvailability;

import java.util.Base64;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class JoinEventFrame extends JFrame {
    private static JoinEventFrame instance;
    private String userName;
    private Event event;
    private int maxAvail;
    private int[][] grpAvail;
    private boolean[][] userAvail;
    private JPanel contentPanel;
    private JPanel myAvailPanel;
    private JPanel grpAvailPanel;
    private JSplitPane splitPane;
    private JTextField nameField;
    private JTextField passwordField;
    private static final String HOST = "http://localhost:8080/";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Color AVAIL = new Color(82, 151, 42);
    private static final Color UNAVAIL = new Color(251, 223, 223);    

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

    // Init UI
    private void initUI() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
    
        // Login Panel right below the title
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        loginPanel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        loginPanel.add(nameField);
        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(10);
        loginPanel.add(passwordField);
        JButton signInButton = new JButton("Register / Sign In");
        signInButton.addActionListener(e -> signIn());
        loginPanel.add(signInButton);
        contentPanel.add(loginPanel, BorderLayout.NORTH);
        
        // Split pane for My Availability and Group Availability
        myAvailPanel = new JPanel(new BorderLayout());
        grpAvailPanel = new JPanel(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   myAvailPanel, grpAvailPanel);
        splitPane.setResizeWeight(0.4);
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        // Refresh button at the bottom
        JButton refreshButton = new JButton("Refresh Availability");
        refreshButton.addActionListener(e -> updateAvailability());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    // Update UI to display availabilities
    private void updateUI() {
        // Remove all components in the content panel
        contentPanel.removeAll();
        contentPanel.revalidate();

        // Different UIs for guest and user
        if (userName == null) {
            JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
            loginPanel.add(new JLabel("Name:"));
            nameField = new JTextField(10);
            loginPanel.add(nameField);
            loginPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField(10);
            loginPanel.add(passwordField);
            JButton signInButton = new JButton("Register / Sign In");
            signInButton.addActionListener(e -> signIn());
            loginPanel.add(signInButton);
            contentPanel.add(loginPanel, BorderLayout.NORTH);
            
            JLabel placeholderLabel = new JLabel("Please register or sign in to mark your availability.");
            placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
            myAvailPanel = new JPanel(new BorderLayout());
            myAvailPanel.add(placeholderLabel, BorderLayout.CENTER);
            grpAvailPanel = new JPanel(new BorderLayout());
            createAvailabilityPanel(grpAvailPanel, "Group Availability", true);
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                    myAvailPanel, grpAvailPanel);
            splitPane.setResizeWeight(0.4);
            contentPanel.add(splitPane, BorderLayout.CENTER);
        } else {
            myAvailPanel = new JPanel(new BorderLayout());
            createAvailabilityPanel(myAvailPanel, "My Availability", false);
            grpAvailPanel = new JPanel(new BorderLayout());
            createAvailabilityPanel(grpAvailPanel, "Group Availability", true);
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                    myAvailPanel, grpAvailPanel);
            splitPane.setResizeWeight(0.5);
            contentPanel.add(splitPane, BorderLayout.CENTER);
        }
        
        // Refresh button at the bottom
        JButton refreshButton = new JButton("Refresh Availability");
        refreshButton.addActionListener(e -> updateAvailability());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.repaint();
    }
    
    // Helper functions for creating sample availability panels
    private void createAvailabilityPanel(JPanel panel, String title, boolean isGroup) {
        String[] dates = event.dates.split("-");
        panel.add(new JLabel(title, JLabel.CENTER), BorderLayout.NORTH);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(event.endTime-event.startTime+1, dates.length));
        for (int i = 0; i < dates.length; i++) {
            mainPanel.add(new JLabel(dates[i], JLabel.CENTER));
        }
        for (int time = event.startTime; time < event.endTime; time++) {
            for (int j = 0; j < dates.length; j++) {
                int i = time - event.startTime;
                JPanel hourPanel = new JPanel();
                hourPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
                hourPanel.setToolTipText(time + ":00 - " + (time + 1) + ":00");
                if (isGroup) {
                    if (maxAvail != 0) {
                        double t = (double) grpAvail[i][j] / maxAvail;
                        int r = (int) (255 * (1-t) + 82 * t);
                        int g = (int) (255 * (1-t) + 151 * t);
                        int b = (int) (255 * (1-t) + 42 * t);
                        hourPanel.setBackground(new Color(r, g, b));
                    } else {
                        hourPanel.setBackground(Color.white);
                    }
                } else {
                    final int finalI = i;
                    final int finalJ = j;
                    hourPanel.setBackground(userAvail[i][j] ? AVAIL : UNAVAIL);
                    hourPanel.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            userAvail[finalI][finalJ] = !userAvail[finalI][finalJ];
                            hourPanel.setBackground(userAvail[finalI][finalJ] ? AVAIL : UNAVAIL);
                            hourPanel.revalidate();
                            hourPanel.repaint();
                        }
                    });
                }
                mainPanel.add(hourPanel);
            }
        }
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
    }
    
    // Send sign-in request
    private void signIn() {
        logMessage("Calling signIn");
        // Check input fields
        String name = nameField.getText();
        String password = passwordField.getText();
        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please input fill in both name and password.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Register user
        byte[] bytesPwd = password.getBytes();
        String base64Pwd = Base64.getEncoder().encodeToString(bytesPwd);
        String utf8Name = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String utf8Pwd = URLEncoder.encode(base64Pwd, StandardCharsets.UTF_8);
        String queryParams = String.format("eventID=%d&name=%s&password=%s", 
            event.eventID, utf8Name, utf8Pwd);
        logMessage("Sign-in request: " + queryParams);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(HOST + "users/register?" + queryParams))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                userName = utf8Name;
                logMessage("Signed in successfully as " + userName);
                JOptionPane.showMessageDialog(this, 
                    "You have signed in successfully!",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                logMessage("Failed to sign in");
                JOptionPane.showMessageDialog(this, 
                    "Name conflict or wrong password.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshAvailability();
    }

    // Update user availability to the server
    private void updateAvailability() {
        logMessage("Calling updateAvailability.");
        StringBuilder avail = new StringBuilder();
        for (int i = 0; i < userAvail.length; i++) {
            for (int j = 0; j < userAvail[i].length; j++) {
                avail.append(userAvail[i][j] ? '1' : '0');
            }
        }
        String queryParams = String.format("eventID=%d&name=%s&userAvailability=%s", 
            event.eventID, userName, avail.toString());
        logMessage("Availability update request: " + queryParams);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(HOST + "availability/update?" + queryParams))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logMessage("Updated successfully");
                JOptionPane.showMessageDialog(this, 
                    "Your availability has been updated! ",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                logMessage("Failed to update. Error: " + response.body());
                JOptionPane.showMessageDialog(this, 
                    "Failed to update. Error: " + response.body(),
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshAvailability();
    }

    // Load up-to-date availabilities
    private void refreshAvailability() {
        logMessage("Calling refreshAvailability.");
        // Init matrices
        int days = event.dates.split("-").length;
        int hours = event.endTime - event.startTime;
        grpAvail = new int[hours][days];
        userAvail = new boolean[hours][days];
        // Fetch user availability (if applicable)
        if (userName != null) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HOST + "availability/" + event.eventID + "/" + userName))
                .GET()
                .build();
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                logMessage("Received user availability: " + response.body());
                if (response.statusCode() == 200 && !response.body().isEmpty()) {
                    UserAvailability ua = objectMapper.readValue(response.body(), UserAvailability.class);
                    if (!ua.userAvailability.isEmpty()) {
                        int index = 0;
                        for (int i = 0; i < hours; i++) {
                            for (int j = 0; j < days; j++) {
                                userAvail[i][j] = (ua.userAvailability.charAt(index) == '1');
                                index++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Fetch group availability
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(HOST + "availability/group/" + event.eventID))
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logMessage("Received group availability: " + response.body());
            if (response.statusCode() == 200) {
                if (!response.body().equals("none")) {
                    maxAvail = 0;
                    int index = 0;
                    String[] avail = response.body().split("-");
                    for (int i = 0; i < hours; i++) {
                        for (int j = 0; j < days; j++) {
                            grpAvail[i][j] = Integer.parseInt(avail[index]);
                            if (grpAvail[i][j] > maxAvail) {
                                maxAvail = grpAvail[i][j];
                            }
                            index++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateUI();
    }

    // Helper function for logging
    private void logMessage(String msg) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss] ");
        String formattedTime = currentTime.format(formatter);
        System.out.println(formattedTime + msg);
    }

    // Make sure only one such frame can be created
    public static JoinEventFrame getInstance(Event event) {
        if (instance == null || !instance.isDisplayable()) {
            instance = new JoinEventFrame();
        }
        instance.setEvent(event);
        return instance;
    }

    // Setter for event config
    private void setEvent(Event event) {
        logMessage("Calling setEvent: " + event);
        this.event = event;
        setTitle("When2Meet Client: Join Event [" + event.eventName +
                 "] (ID = " + event.eventID + ")");
        refreshAvailability();
    }

}