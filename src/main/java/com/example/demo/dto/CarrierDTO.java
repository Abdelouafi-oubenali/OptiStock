package com.example.demo.dto;

import com.example.demo.enums.CarrierStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierDTO {
    private UUID id;

    @NotNull(message = "Le nom du transporteur est requis")
    private String name;

    private String contactEmail;
    private String contactPhone;
    private BigDecimal baseShippingRate;
    private Integer maxDailyCapacity;
    private Integer currentDailyShipments;
    private LocalTime cutOffTime;
    private CarrierStatus status;
}