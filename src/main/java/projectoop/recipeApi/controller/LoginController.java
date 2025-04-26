package projectoop.recipeApi.controller;

import projectoop.recipeApi.model.User;
import projectoop.recipeApi.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if ("admin".equals(user.getUsername()) && "password".equals(user.getPassword())) {
            String token = JwtUtil.generateToken(user.getUsername());
            response.put("message", "Authentication successful");
            response.put("token", "Bearer " + token);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}