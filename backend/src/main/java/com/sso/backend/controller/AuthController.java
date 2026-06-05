package com.sso.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Auth Controller — endpoints related to user authentication info
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Map<String, Object> attributes = user.getAttributes();
        
        // Extract roles from GrantedAuthorities
        List<String> roles = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5)) // remove "ROLE_"
                .toList();

        return ResponseEntity.ok(Map.of(
            "id", attributes.getOrDefault("sub", ""),
            "email", attributes.getOrDefault("email", ""),
            "name", attributes.getOrDefault("name", ""),
            "username", attributes.getOrDefault("preferred_username", ""),
            "roles", roles
        ));
    }

    /** GET /api/public/health — Public health check (no auth required) */
    @GetMapping("/public/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Inventory Backend",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
