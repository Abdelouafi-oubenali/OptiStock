package com.example.demo.dto;

import com.example.demo.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {
    private UUID id;

    @NotNull(message = "Le trackingNumber est requis !!")
    private String trackingNumber;

    @NotNull(message = "Le status shipment est requis !")
    private ShipmentStatus status;

    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    @NotNull(message = "Le sales_order_id est requis !")
    private UUID salesOrderId;
}