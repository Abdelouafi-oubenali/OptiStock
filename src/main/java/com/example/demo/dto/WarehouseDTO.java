package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
public class WarehouseDTO {

    private UUID id;

    @NotEmpty(message = "le nome de warehosed et obligatoir")
    private String name  ;

    @NotEmpty(message = "la ville de warhosed et obligatoir")
    private String ville ;

    private boolean active;




}
