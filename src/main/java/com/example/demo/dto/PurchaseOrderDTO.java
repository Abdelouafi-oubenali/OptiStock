package com.example.demo.dto;

import com.example.demo.enums.PurchaseOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDTO {
    private UUID id;

    @NotNull(message = "L'id de supplier est obligatoire !!")
    private UUID supplierId;

    @NotNull(message = "L'id de user createur est obligatoire !")
    private UUID createdByUserId;

    private UUID approvedByUserId;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expectedDelivery;

    private BigDecimal totalAmount;
    private List<PurchaseOrderLineDTO> orderLines;
}