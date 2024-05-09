package when2meet.server.service;

import when2meet.server.model.User;
import when2meet.server.model.UserId;
import when2meet.server.model.Availability;
import when2meet.server.model.AvailabilityId;
import when2meet.server.repository.AvailabilityRepository;
import when2meet.server.repository.EventRepository;
import when2meet.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public String updateOrCreateAvailability(int eventID, String name, String userAvailability) {
        // Check if the eventID exists
        if (!eventRepository.existsById(eventID)) {
            return "failure: Event does not exist";
        }

        // Check if the name is a registered user for the event
        Optional<User> user = userRepository.findById(new UserId(eventID, name));
        if (!user.isPresent()) {
            return "failure: User is not registered for this event";
        }

        AvailabilityId id = new AvailabilityId(eventID, name);
        Optional<Availability> existingAvailability = availabilityRepository.findById(id);

        if (existingAvailability.isPresent()) {
            // Update existing availability
            Availability availability = existingAvailability.get();
            availability.setUserAvailability(userAvailability);
            availabilityRepository.save(availability);
        } else {
            // Create new availability
            Availability newAvailability = new Availability(eventID, name, userAvailability);
            availabilityRepository.save(newAvailability);
        }
        
        return "success";
    }

    public Optional<Availability> getAvailabilityById(int eventID, String name) {
        return availabilityRepository.findById(new AvailabilityId(eventID, name));
    }

    public String getGroupAvailabilityById(int eventID) {
        List<Availability> availabilities = availabilityRepository.findByEventID(eventID);
        if (availabilities.isEmpty()) {
            return "none";
        }

        int length = availabilities.get(0).getUserAvailability().length();
        int[] sums = new int[length];

        availabilities.forEach(availability -> {
            for (int i = 0; i < length; i++) {
                sums[i] += availability.getUserAvailability().charAt(i) - '0';
            }
        });

        return IntStream.of(sums)
                .mapToObj(String::valueOf)
                .reduce((a, b) -> a + "-" + b)
                .orElse("");
    }
}
