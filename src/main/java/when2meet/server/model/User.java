package when2meet.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@IdClass(UserId.class)
public class User {
    @Id
    private int eventID;
    @Id
    private String name;

    private String password;

    // Constructors
    public User() {
    }

    public User(int eventID, String name, String password) {
        this.eventID = eventID;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
