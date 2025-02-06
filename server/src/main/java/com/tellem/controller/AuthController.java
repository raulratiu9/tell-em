package com.tellem.controller;

import com.tellem.repository.UserRepository;
import com.tellem.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    @Value("${spring.security.oauth2.client.provider.facebook.authorization-uri}")
    private String facebookRedirect;
    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String googleRedirect;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public AuthController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request) {
        OAuth2User user = (OAuth2User) request.getUserPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        String username = user.getAttribute("username");
        String jwt = jwtService.generateToken(username);

        return ResponseEntity.ok(Map.of("jwt", jwt, "email", username));
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

    @GetMapping("/oauth2/authorization/{provider}")
    public void oauth2Login(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String oauthUrl = getOAuthProviderUrl(provider);
        System.out.print(oauthUrl + "oauthurl");

        if (oauthUrl != null) {
            response.sendRedirect(oauthUrl);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported provider");
        }
    }

    @PostMapping("/googleToken")
    public String exchangeCodeForToken(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String redirectUri = googleRedirectUri;

        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";
        String clientId = googleClientId;
        String clientSecret = googleClientSecret;

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        Map<String, String> response = restTemplate.postForObject(tokenUrl, params, Map.class);
        return response.get("access_token");
    }

    private String getOAuthProviderUrl(String provider) {
        if (provider.equals("facebook")) {
            return facebookRedirect;
        } else if (provider.equals("google")) {
            return googleRedirect;
        } else {
            return null;
        }
    }
}

