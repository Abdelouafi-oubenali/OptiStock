package com.example.demo.entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;
import org.springframework.cglib.proxy.Factory;

import java.util.UUID;

@Entity
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    private Integer qtyOnHand ;
    private Integer qtyReserved ;
    private String referenceDocument ;

    // Relation avec Warehouse
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    // Relation avec Product (vous devrez créer cette entité)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
