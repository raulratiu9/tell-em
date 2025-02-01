package com.tellem.controller.authentication;

import com.tellem.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class LoginController {

    private final JwtService jwtService;

    public LoginController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/")
    public ResponseEntity<?> home(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body("User not authenticated");
        }
        String username = authentication.getName();
        Object userData = authentication.getPrincipal();
        String jwt = jwtService.generateToken(username);
        Map<String, Object> response = Map.of(
                "userData", userData,
                "jwt", jwt
        );
        return ResponseEntity.ok(response);
    }
}