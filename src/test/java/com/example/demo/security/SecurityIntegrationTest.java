package com.example.demo.security;

import com.example.demo.config.BaseIntegrationTest;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        createUser("admin@test.com", "password", Role.ADMIN);
        createUser("wm@test.com", "password", Role.WAREHOUSE_MANAGER);
    }

    private void createUser(String email, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }

    private String getToken(String email, String password) throws Exception {
        String body = """
            {
              "email": "%s",
              "password": "%s"
            }
            """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        return objectMapper.readTree(response)
                .get("accessToken")
                .asText();
    }

    private String getRefreshToken(String email, String password) throws Exception {
        String body = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        return objectMapper.readTree(response)
                .get("refreshToken")
                .asText();
    }


    @Test
    void refresh_without_token_should_return_401() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void invalid_refresh_token_should_be_rejected() throws Exception {
        String body = """
        {
          "refreshToken": "INVALID_REFRESH_TOKEN"
        }
        """;

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void expired_refresh_token_should_be_rejected() throws Exception {
        String expiredRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsImV4cCI6MTUxNjIzOTAyMiwiaWF0IjoxNTE2MjM5MDIyfQ."
                + "expired_signature";

        String body = """
        {
          "refreshToken": "%s"
        }
        """.formatted(expiredRefreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }




    @Test
    void valid_refresh_token_should_return_new_access_token() throws Exception {
        String refreshToken = getRefreshToken("admin@test.com", "password");

        String body = """
        {
          "refreshToken": "%s"
        }
        """.formatted(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }


    @Test
    void admin_endpoint_without_token_should_return_401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\":\"Unauthorized - Authentication required\"}"));
    }

    @Test
    void admin_role_should_access_products() throws Exception {
        String adminToken = getToken("admin@test.com", "password");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void warehouse_manager_should_not_access_products() throws Exception {
        String token = getToken("wm@test.com", "password");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\"error\":\"Forbidden - Insufficient permissions\"}"));
    }

    @Test
    void invalid_token_should_be_rejected() throws Exception {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjM5MDIyfQ.invalid_signature_here";

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformed_token_without_dots_should_be_rejected() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer INVALID_TOKEN_WITHOUT_DOTS"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expired_token_should_be_rejected() throws Exception {

        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsImV4cCI6MTUxNjIzOTAyMiwiaWF0IjoxNTE2MjM5MDIyfQ.expired_signature";

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}