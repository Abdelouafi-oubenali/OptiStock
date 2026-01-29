package com.example.demo.entity;

import com.example.demo.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    String trackingNumber ;

    @Enumerated(EnumType.STRING)
    ShipmentStatus status  ;

    LocalDateTime plannedDate ;
    LocalDateTime shippedDate ;
    LocalDateTime deliveredDate ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

}
