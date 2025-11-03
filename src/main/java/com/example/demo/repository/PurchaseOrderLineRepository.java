package com.example.demo.repository;

import com.example.demo.entity.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, UUID> {
    List<PurchaseOrderLine> findByPurchaseOrderId(UUID purchaseOrderId);
    void deleteByPurchaseOrderId(UUID purchaseOrderId);
}