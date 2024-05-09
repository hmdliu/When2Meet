package when2meet.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "availability")
@IdClass(AvailabilityId.class)
public class Availability {
    @Id
    private int eventID;
    @Id
    private String name;

    private String userAvailability; // String of 0s and 1s

    // Constructors
    public Availability() {
    }

    public Availability(int eventID, String name, String userAvailability) {
        this.eventID = eventID;
        this.name = name;
        this.userAvailability = userAvailability;
    }

    // Getters and Setters
    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAvailability() {
        return userAvailability;
    }

    public void setUserAvailability(String userAvailability) {
        this.userAvailability = userAvailability;
    }
}
