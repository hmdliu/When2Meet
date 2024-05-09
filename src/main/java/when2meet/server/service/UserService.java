package when2meet.server.service;

import when2meet.server.model.User;
import when2meet.server.model.UserId;
import when2meet.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User registerUser(int eventID, String name, String password) {
        User newUser = new User(eventID, name, password);
        return userRepository.save(newUser);
    }

    public String registerOrAuthenticateUser(int eventID, String name, String base64EncodedPassword) {
        // Decode the Base64-encoded password
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedPassword);
        String decodedPassword = new String(decodedBytes, StandardCharsets.UTF_8);

        // Check for an existing user
        Optional<User> existingUser = userRepository.findById(new UserId(eventID, name));
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getPassword().equals(decodedPassword)) {
                return "success"; // Password matches
            } else {
                return "failure"; // Password does not match
            }
        } else {
            // No user found, create a new one
            User newUser = new User(eventID, name, decodedPassword);
            userRepository.save(newUser);
            return "success"; // New user created successfully
        }
    }

    public Optional<User> getUserById(int eventID, String name) {
        return userRepository.findById(new UserId(eventID, name));
    }
}
