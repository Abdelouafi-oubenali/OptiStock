package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {

    private UUID id;

    @NotNull(message = "qtyOnHand est requis")
    private Integer qtyOnHand;

    @NotNull(message = "qtyReserved est requis")
    private Integer qtyReserved;

    @NotNull(message = "referenceDocument est requis")
    private String referenceDocument;

    @NotNull(message = "le warehouse est requis")
    private UUID warehouse_id;

    @NotNull(message = "le product est requis")  // Corrigez le message
    private UUID product_id;
}