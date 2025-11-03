package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class SupplierDTO {
    private UUID id;

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    private String name;

    private String contactInfo;

    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean active = true;
}