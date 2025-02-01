package com.tellem.controller;

import com.tellem.model.User;
import com.tellem.repository.UserRepository;
import com.tellem.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public ResponseEntity login(HttpServletRequest request) {
        OAuth2User user = (OAuth2User) request.getUserPrincipal();
        String jwt = jwtService.generateToken(user.getAttributes().get("username").toString());
        System.out.println("s-o generat: " + jwt);
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @GetMapping("/token")
    public ResponseEntity<?> getToken(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        String username = authentication.getName();
        String jwt = jwtService.generateToken(username);
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}

