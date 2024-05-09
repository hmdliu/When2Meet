package when2meet.server.controller;

import when2meet.server.model.Availability;
import when2meet.server.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @PostMapping("/update")
    public ResponseEntity<String> updateAvailability(@RequestParam int eventID,
                                                     @RequestParam String name,
                                                     @RequestParam String userAvailability) {
        try {
            String result = availabilityService.updateOrCreateAvailability(eventID, name, userAvailability);
            if ("success".equals(result)) {
                return ResponseEntity.ok("Availability update/create successful.");
            } else {
                // Use the failure message from the service
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            // Log the exception details here for debugging purposes
            return ResponseEntity.status(500).body("Failure due to internal error: " + e.getMessage());
        }
    }

    @GetMapping("/{eventID}/{name}")
    public ResponseEntity<Availability> getAvailabilityById(@PathVariable int eventID,
                                                            @PathVariable String name) {
        return availabilityService.getAvailabilityById(eventID, name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/group/{eventID}")
    public ResponseEntity<String> getGroupAvailabilityByEventId(@PathVariable int eventID) {
        String groupAvailability = availabilityService.getGroupAvailabilityById(eventID);
        return ResponseEntity.ok(groupAvailability);
    }
}
