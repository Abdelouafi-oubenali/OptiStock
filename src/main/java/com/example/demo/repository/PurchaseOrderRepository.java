package com.example.demo.repository;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    List<PurchaseOrder> findBySupplierId(UUID supplierId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    List<PurchaseOrder> findByCreatedById(UUID userId);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.createdBy.id = :userId")
    List<PurchaseOrder> findByCreatedByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.supplier.id = :supplierId")
    Long countBySupplierId(@Param("supplierId") UUID supplierId);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.expectedDelivery < CURRENT_DATE AND po.status IN :statuses")
    List<PurchaseOrder> findOverdueOrders(@Param("statuses") List<PurchaseOrderStatus> statuses);
}