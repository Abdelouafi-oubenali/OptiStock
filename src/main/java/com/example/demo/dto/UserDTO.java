package com.example.demo.dto;

import com.example.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}