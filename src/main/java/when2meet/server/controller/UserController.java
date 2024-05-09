package when2meet.server.controller;

// import when2meet.server.model.User;
import when2meet.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam int eventID,
                                               @RequestParam String name,
                                               @RequestParam String password) {
        String result = userService.registerOrAuthenticateUser(eventID, name, password);
        if ("success".equals(result)) {
            return ResponseEntity.ok("User registration/authentication successful.");
        } else {
            return ResponseEntity.badRequest().body("User registration/authentication failed. Password mismatch.");
        }
    }

    // @GetMapping("/{eventID}/{name}")
    // public ResponseEntity<User> getUserById(@PathVariable int eventID,
    //                                         @PathVariable String name) {
    //     return userService.getUserById(eventID, name)
    //             .map(ResponseEntity::ok)
    //             .orElseGet(() -> ResponseEntity.notFound().build());
    // }
}
