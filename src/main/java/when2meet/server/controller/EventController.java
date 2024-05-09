package when2meet.server.controller;

import when2meet.server.model.Event;
import when2meet.server.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestParam String eventName,
                                             @RequestParam String dates,
                                             @RequestParam int startTime,
                                             @RequestParam int endTime) {
        Event event = eventService.createEvent(eventName, dates, startTime, endTime);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{eventID}")
    public ResponseEntity<Event> getEventById(@PathVariable int eventID) {
        return eventService.getEventById(eventID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
