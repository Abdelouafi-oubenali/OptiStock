package com.example.demo.controller;

import com.example.demo.dto.RegisterDTO;
import com.example.demo.dto.UserDTO; // Assurez-vous d'utiliser le bon DTO
import com.example.demo.dto.loginDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid; // ← IMPORTANT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated; // ← IMPORTANT
import org.springframework.web.bind.MethodArgumentNotValidException; // ← Pour gérer les erreurs
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody loginDTO loginData) {

        String email = loginData.getEmail();
        String password = loginData.getPassword();

        boolean success = authService.login(email, password);

        if (success) {
            return ResponseEntity.ok("Login successful!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userMapper.registerDtoToEntity(registerDTO);
            User savedUser = authService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userMapper.toDTO(savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}