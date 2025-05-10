package projectoop.recipeApi.controller;

import projectoop.recipeApi.model.User;
import projectoop.recipeApi.model.UserRole;
import projectoop.recipeApi.repository.UserRepository;
import projectoop.recipeApi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("⚠️ Username already exists.");
        }

        // ✅ Set default role to CLIENT (not a String)
        user.setRole(UserRole.CLIENT);

        // ✅ Encrypt password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return ResponseEntity.ok("✅ User registered successfully.");
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> dbUserOpt = userRepository.findByUsername(user.getUsername());

        System.out.println("🔎 Login attempt for username: " + user.getUsername());

        if (dbUserOpt.isEmpty()) {
            System.out.println("❌ Username not found in DB");
            return ResponseEntity.status(401).body("❌ Invalid username or password.");
        }

        User dbUser = dbUserOpt.get();

        System.out.println("📂 Found user in DB: " + dbUser.getUsername());
        System.out.println("🔐 Hashed password in DB: " + dbUser.getPassword());
        System.out.println("🧪 Password matches? " + passwordEncoder.matches(user.getPassword(), dbUser.getPassword()));

        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return ResponseEntity.status(401).body("❌ Invalid username or password.");
        }

        // ✅ Generate JWT with username and role
        String token = JwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("message", "✅ Authentication successful");
        response.put("token", "Bearer " + token);

        System.out.println("🎫 Token generated: " + token);
        System.out.println("👤 Role: " + dbUser.getRole());

        return ResponseEntity.ok(response);
    }
}
