package when2meet.server.model;

import java.io.Serializable;
import java.util.Objects;

public class AvailabilityId implements Serializable {
    private int eventID;
    private String name;

    // Constructors
    public AvailabilityId() {
    }

    public AvailabilityId(int eventID, String name) {
        this.eventID = eventID;
        this.name = name;
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

    @Override
    public int hashCode() {
        return Objects.hash(eventID, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailabilityId that = (AvailabilityId) o;
        return eventID == that.eventID &&
               Objects.equals(name, that.name);
    }
}
