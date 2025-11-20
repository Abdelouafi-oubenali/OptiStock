package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestAuthController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Ceci est un endpoint public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userEndpoint() {
        return "Ceci est un endpoint USER";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Ceci est un endpoint ADMIN";
    }

    @GetMapping("/authenticated")
    public String authenticatedEndpoint() {
        return "Ceci est un endpoint pour tout utilisateur authentifié";
    }
}