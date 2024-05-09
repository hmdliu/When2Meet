package when2meet.server.model;

import java.io.Serializable;
import java.util.Objects;

public class UserId implements Serializable {
    private int eventID;
    private String name;

    // Constructors
    public UserId() {
    }

    public UserId(int eventID, String name) {
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
        UserId userId = (UserId) o;
        return eventID == userId.eventID &&
               Objects.equals(name, userId.name);
    }
}
