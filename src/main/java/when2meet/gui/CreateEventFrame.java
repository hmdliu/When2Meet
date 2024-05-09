
package when2meet.gui;

import java.net.URI;
import java.net.http.*;
import java.net.URLEncoder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;

import when2meet.gui.mapper.Event;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;

public class CreateEventFrame extends JFrame {
    private static CreateEventFrame instance;
    private static final int MAX_SELECTIONS = 5;
    private static final int NUM_CANDIDATES = 30;
    private static final String HOST = "http://localhost:8080/";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private JPanel mainPanel;

    // Constructor
    public CreateEventFrame() {
        setTitle("When2Meet Client: Create Event");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(200, 200);
        setLocationRelativeTo(null);
        initMenu();
        initUI();
        pack();
        setVisible(true);
        logMessage("CreateEventFrame created.");
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
        // Top panel for event name
        JPanel topPanel = new JPanel();
        JTextField eventNameField = new JTextField(20);
        topPanel.add(new JLabel("New Event Name:"));
        topPanel.add(eventNameField);
        add(topPanel, BorderLayout.NORTH);
        
        // Main panel for date and time selections
        mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Calendar selection part
        JPanel calendarPanel = new JPanel(new GridLayout(1, 2, 0, 10));
        calendarPanel.setBorder(BorderFactory.createTitledBorder(
            "What dates might work? (add to right panel)"
        ));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy (EEEE)");
        Calendar cal = Calendar.getInstance();
        DefaultListModel<String> candidateModel = new DefaultListModel<>();
        DefaultListModel<String> selectedModel = new DefaultListModel<>();
        for (int i = 0; i < NUM_CANDIDATES; i++) {
            candidateModel.addElement(sdf.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }
        JList<String> candidateDates = new JList<>(candidateModel);
        JList<String> selectedDates = new JList<>(selectedModel);
        candidateDates.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (selectedModel.size() < MAX_SELECTIONS) {
                    moveDate(candidateDates, candidateModel, selectedModel);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, 
                        "You may select at most " + MAX_SELECTIONS + " days.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        selectedDates.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                moveDate(selectedDates, selectedModel, candidateModel);
            }
        });
        calendarPanel.add(new JScrollPane(candidateDates));
        calendarPanel.add(new JScrollPane(selectedDates));
        mainPanel.add(calendarPanel, BorderLayout.NORTH);
        
        // Time selection part
        JPanel timePanel = new JPanel(new GridLayout(1, 4, 10, 10));
        timePanel.setBorder(BorderFactory.createTitledBorder("What times might work?"));
        String[] times = {"6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                          "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM",
                          "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"};
        JComboBox<String> startTimeCombo = new JComboBox<>(Arrays.copyOf(times, times.length-1));
        JComboBox<String> endTimeCombo = new JComboBox<>(times);
        startTimeCombo.setSelectedItem("9:00 AM");
        endTimeCombo.setSelectedItem("5:00 PM");
        timePanel.add(new JLabel("No earlier than:"));
        timePanel.add(startTimeCombo);
        timePanel.add(new JLabel("No later than:"));
        timePanel.add(endTimeCombo);
        startTimeCombo.addActionListener(e -> {
            int start = startTimeCombo.getSelectedIndex();
            endTimeCombo.removeAllItems();
            for (int i = start + 1; i < times.length; i++) {
                endTimeCombo.addItem(times[i]);
            }
        });
        mainPanel.add(timePanel, BorderLayout.CENTER);
        
        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        JButton createButton = new JButton("Create Event");
        createButton.addActionListener((ActionEvent e) -> {
            // Check event name
            String eventName = eventNameField.getText();
            if (eventName.length() == 0) {
                JOptionPane.showMessageDialog(mainPanel, 
                    "The event name cannot be empty.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Check candidate dates
            ArrayList<String> dateList = new ArrayList<>();
            for (int i = 0; i < selectedModel.size(); i++) {
                dateList.add(selectedModel.get(i));
            }
            if (dateList.size() == 0) {
                JOptionPane.showMessageDialog(mainPanel, 
                    "No date has been selected.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Check time duration
            int startTimeIndex = startTimeCombo.getSelectedIndex();
            int endTimeIndex = endTimeCombo.getSelectedIndex();
            logMessage(
                "Submitting event [" + eventName + "] with candidate dates " + 
                dateList + ". Starting from " + times[startTimeIndex] +
                " to " + times[endTimeIndex] + "."
            );
            submitEvent(eventName, dateList, startTimeIndex+6, endTimeIndex+6);
        });
        bottomPanel.add(createButton);
        add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    // Add or remove selected dates
    private void moveDate(JList<String> sourceList, DefaultListModel<String> sourceModel,
                          DefaultListModel<String> targetModel) {
        String selected = sourceList.getSelectedValue();
        if (selected != null) {
            sourceModel.removeElement(selected);
            targetModel.addElement(selected);
            // Sort the target model
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < targetModel.size(); i++) {
                list.add(targetModel.get(i));
            }
            Collections.sort(list);
            targetModel.removeAllElements();
            for (String s : list) {
                targetModel.addElement(s);
            }
        }
    }

    // Send event info to the server
    private void submitEvent(String eventName, ArrayList<String> dateList,
                             int startTime, int endTime) {
        // Convert date format & merge to a single string
        StringBuilder formattedDates = new StringBuilder();
        SimpleDateFormat srcFormat = new SimpleDateFormat("MM/dd/yyyy (EEEE)");
        SimpleDateFormat dstFormat = new SimpleDateFormat("MM/dd EEE");
        for (String dateStr : dateList) {
            try {
                Date date = srcFormat.parse(dateStr);
                formattedDates.append(dstFormat.format(date)).append("-");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (formattedDates.length() > 0) {
            formattedDates.setLength(formattedDates.length() - 1);
        }
        String dates = formattedDates.toString();
        // Send request
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8);
        String encodedDates = URLEncoder.encode(dates, StandardCharsets.UTF_8);
        String queryParams = String.format("eventName=%s&dates=%s&startTime=%d&endTime=%d", 
            encodedEventName, encodedDates, startTime, endTime);
        logMessage("Event request: " + queryParams);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(HOST + "events/create?" + queryParams))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logMessage("Event created successfully: " + response.body());
                Event event = objectMapper.readValue(response.body(), Event.class);
                dispose();
                logMessage("Invoking JoinEventFrame.");
                JoinEventFrame.getInstance(event);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + response.body(),
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                logMessage("Failed to create event. Status code: " +
                           response.statusCode() + ". Info: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper function for logging
    private void logMessage(String msg) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss] ");
        String formattedTime = currentTime.format(formatter);
        System.out.println(formattedTime + msg);
    }

    // Make sure only one such frame can be created
    public static CreateEventFrame getInstance() {
        if (instance == null || !instance.isDisplayable()) {
            instance = new CreateEventFrame();
        }
        return instance;
    }
}