package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository ;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthController(AuthService authService , UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository ;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterDTO registerDTO,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        log.info("REGISTER_ATTEMPT - Email: {}, IP: {}, Time: {}, Agent: {}",
                registerDTO.getEmail(),
                ipAddress,
                LocalDateTime.now(),
                userAgent
        );

        try {
            User response = authService.register(registerDTO);

            log.info("REGISTER_SUCCESS - Email: {}, UserId: {}, Time: {}",
                    registerDTO.getEmail(),
                    response.getId(),
                    LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("REGISTER_FAILURE - Email: {}, IP: {}, Error: {}, Time: {}",
                    registerDTO.getEmail(),
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest authRequest,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        log.info("LOGIN_ATTEMPT - Email: {}, IP: {}, Time: {}, Agent: {}",
                authRequest.getEmail(),
                ipAddress,
                LocalDateTime.now(),
                userAgent
        );

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );


            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() ->  new UsernameNotFoundException("User not found")) ;


            String accessToken = jwtUtil.generateAccessToken(authRequest.getEmail() , user.getRole()  );
            String refreshToken = jwtUtil.generateRefreshToken(authRequest.getEmail() , user.getRole());

            AuthResponse response = new AuthResponse(accessToken, refreshToken);

            log.info("LOGIN_SUCCESS - Email: {}, IP: {}, TokenIssued: {}, Time: {}",
                    authRequest.getEmail(),
                    ipAddress,
                    true,
                    LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("LOGIN_FAILURE_INVALID_CREDENTIALS - Email: {}, IP: {}, Time: {}",
                    authRequest.getEmail(),
                    ipAddress,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid email or password\"}");

        } catch (Exception e) {
            log.error("LOGIN_FAILURE - Email: {}, IP: {}, Error: {}, Time: {}",
                    authRequest.getEmail(),
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Login failed\"}");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String refreshToken = requestBody.get("refreshToken");

        log.info("REFRESH_TOKEN_ATTEMPT - IP: {}, Time: {}",
                ipAddress,
                LocalDateTime.now()
        );

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.warn("REFRESH_TOKEN_MISSING - IP: {}, Time: {}",
                    ipAddress,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Refresh token is required\"}");
        }

        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                log.warn("REFRESH_TOKEN_INVALID - IP: {}, Time: {}",
                        ipAddress,
                        LocalDateTime.now()
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid refresh token\"}");
            }

            String email = jwtUtil.extractUsername(refreshToken);
            Role role = jwtUtil.extractRole(refreshToken) ;
            String newAccessToken = jwtUtil.generateAccessToken(email , role );

            log.info("REFRESH_TOKEN_SUCCESS - Email: {}, IP: {}, Time: {}",
                    email,
                    ipAddress,
                    LocalDateTime.now()
            );

            return ResponseEntity.ok(
                    Map.of("accessToken", newAccessToken)
            );

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("REFRESH_TOKEN_EXPIRED - IP: {}, Time: {}",
                    ipAddress,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Refresh token has expired\"}");

        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("REFRESH_TOKEN_INVALID_SIGNATURE - IP: {}, Time: {}",
                    ipAddress,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid refresh token signature\"}");

        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("REFRESH_TOKEN_MALFORMED - IP: {}, Time: {}",
                    ipAddress,
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid refresh token format\"}");

        } catch (Exception e) {
            log.error("REFRESH_TOKEN_ERROR - IP: {}, Error: {}, Time: {}",
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid refresh token\"}");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String authHeader = request.getHeader("Authorization");

        log.info("GET_CURRENT_USER_ATTEMPT - IP: {}, HasAuthHeader: {}, Time: {}",
                ipAddress,
                authHeader != null,
                LocalDateTime.now()
        );

        try {
            Object user = authService.getCurrentUser();

            log.info("GET_CURRENT_USER_SUCCESS - IP: {}, Time: {}",
                    ipAddress,
                    LocalDateTime.now()
            );

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            log.error("GET_CURRENT_USER_FAILURE - IP: {}, Error: {}, Time: {}",
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(
            @PathVariable String email,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();

        log.info("CHECK_EMAIL_ATTEMPT - Email: {}, IP: {}, Time: {}",
                email,
                ipAddress,
                LocalDateTime.now()
        );

        try {
            boolean exists = authService.emailExists(email);

            log.info("CHECK_EMAIL_RESULT - Email: {}, Exists: {}, IP: {}, Time: {}",
                    email,
                    exists,
                    ipAddress,
                    LocalDateTime.now()
            );

            return ResponseEntity.ok().body("{\"exists\": " + exists + "}");

        } catch (Exception e) {
            log.error("CHECK_EMAIL_ERROR - Email: {}, IP: {}, Error: {}, Time: {}",
                    email,
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String authHeader = request.getHeader("Authorization");

        log.info("LOGOUT_ATTEMPT - IP: {}, HasToken: {}, Time: {}",
                ipAddress,
                authHeader != null,
                LocalDateTime.now()
        );

        // هنا يمكنك إضافة منطق لإبطال token إذا كنت تستخدم blacklist
        // authService.invalidateToken(authHeader);

        log.info("LOGOUT_SUCCESS - IP: {}, Time: {}",
                ipAddress,
                LocalDateTime.now()
        );

        return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String token = request.getHeader("Authorization");

        log.info("VALIDATE_TOKEN_ATTEMPT - IP: {}, HasToken: {}, Time: {}",
                ipAddress,
                token != null,
                LocalDateTime.now()
        );

        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("VALIDATE_TOKEN_MISSING - IP: {}, Time: {}",
                    ipAddress,
                    LocalDateTime.now()
            );
            return ResponseEntity.ok("{\"valid\": false, \"reason\": \"No token provided\"}");
        }

        try {
            String jwt = token.substring(7);
            boolean isValid = jwtUtil.validateToken(jwt);

            if (isValid) {
                String email = jwtUtil.extractUsername(jwt);
                log.info("VALIDATE_TOKEN_VALID - Email: {}, IP: {}, Time: {}",
                        email,
                        ipAddress,
                        LocalDateTime.now()
                );
                return ResponseEntity.ok("{\"valid\": true, \"email\": \"" + email + "\"}");
            } else {
                log.warn("VALIDATE_TOKEN_INVALID - IP: {}, Time: {}",
                        ipAddress,
                        LocalDateTime.now()
                );
                return ResponseEntity.ok("{\"valid\": false, \"reason\": \"Invalid token\"}");
            }

        } catch (Exception e) {
            log.error("VALIDATE_TOKEN_ERROR - IP: {}, Error: {}, Time: {}",
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            return ResponseEntity.ok("{\"valid\": false, \"reason\": \"Token validation error\"}");
        }
    }
}