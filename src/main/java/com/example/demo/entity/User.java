package com.example.demo.entity;

import com.example.demo.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Relations avec PurchaseOrder
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<PurchaseOrder> createdPurchaseOrders;

    @OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
    private List<PurchaseOrder> approvedPurchaseOrders;

    // RELATION: Commandes de vente de cet utilisateur
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<SalesOrder> salesOrders;

    // Relation avec Warehouse (pour les managers)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse managedWarehouse;
}