package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderLineDTO {
    private UUID id;

    @NotNull(message = "L'id du produit est obligatoire")
    private UUID productId;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;

    private Integer backorder = 0 ;


    @NotNull(message = "Le prix unitaire est obligatoire")
    private BigDecimal unitPrice;

    // Le totalPrice sera calculé automatiquement
    private BigDecimal totalPrice;

    private String productName;
    private String productSku;
}