package com.example.demo.entity;

import com.example.demo.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // RELATION AVEC USER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relation avec SalesOrderLine
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SalesOrderLine> orderLines;

    // RELATION AVEC SHIPMENT
    @OneToMany(mappedBy = "salesOrder", fetch = FetchType.LAZY)
    private List<Shipment> shipments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    public BigDecimal getOrderTotal() {
//        if (orderLines == null) {
//            return BigDecimal.ZERO;
//        }
//        return orderLines.stream()
//                .map(SalesOrderLine::getLineTotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }

//    public Integer getTotalItems() {
//        if (orderLines == null) {
//            return 0;
//        }
//        return orderLines.stream()
//                .mapToInt(SalesOrderLine::getQuantity)
//                .sum();
//    }
}