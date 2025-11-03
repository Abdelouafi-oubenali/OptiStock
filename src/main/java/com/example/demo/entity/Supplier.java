package com.example.demo.entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    private String name ;
    private  String contactInfo ;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<PurchaseOrder> purchaseOrders;

    @Column(nullable = false)
    private Boolean active = true;

}
