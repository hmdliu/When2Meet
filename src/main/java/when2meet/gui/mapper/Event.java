package when2meet.gui.mapper;

public class Event {
    public int eventID;
    public String eventName;
    public String dates;
    public int startTime;
    public int endTime;

    @Override
    public String toString() {
        return "Event{" +
                "eventID=" + eventID +
                ", eventName='" + eventName + '\'' +
                ", dates='" + dates + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
