package com.example.demo.dto;

import com.example.demo.enums.Role;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;

    // Méthode utilitaire pour le nom complet
    public String getFullName() {
        return firstName + " " + lastName;
    }
}