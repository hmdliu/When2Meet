package when2meet.server.service;

import when2meet.server.model.Event;
import when2meet.server.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Event createEvent(String eventName, String dates, int startTime, int endTime) {
        Event event = new Event(eventName, dates, startTime, endTime);
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(int eventID) {
        return eventRepository.findById(eventID);
    }
}
