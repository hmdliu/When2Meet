package when2meet.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventID;

    private String eventName;
    private String dates;   // Date strings separated with '-'
    private int startTime;  // E.g., 9 for 9 AM
    private int endTime;    // E.g., 17 for 5 PM

    // Constructors
    public Event() {
    }

    public Event(String eventName, String dates, int startTime, int endTime) {
        this.eventName = eventName;
        this.dates = dates;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
