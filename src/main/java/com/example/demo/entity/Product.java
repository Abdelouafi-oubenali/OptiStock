package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Relation avec Inventory
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories;

    // Relation avec PurchaseOrderLine
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<PurchaseOrderLine> purchaseOrderLines;

    // RELATION: Lignes de commande de vente pour ce produit
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<SalesOrderLine> salesOrderLines;
}