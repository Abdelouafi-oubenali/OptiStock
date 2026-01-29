package com.example.demo.entity;

import com.example.demo.enums.CarrierStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String contactEmail;
    private String contactPhone;
    private BigDecimal baseShippingRate;
    private Integer maxDailyCapacity;
    private Integer currentDailyShipments;
    private LocalTime cutOffTime;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status;

}