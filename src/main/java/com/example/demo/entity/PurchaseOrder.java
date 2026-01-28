package com.example.demo.entity;

import com.example.demo.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_order")
@Data
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expectedDelivery;

    // ADD this field if it doesn't exist
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // IMPORTANT: Add cascade = CascadeType.ALL to automatically persist/delete order lines
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderLine> orderLines;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PurchaseOrderStatus.DRAFT;
        }
    }
}
