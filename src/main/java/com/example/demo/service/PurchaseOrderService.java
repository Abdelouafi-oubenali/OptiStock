package com.example.demo.service;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.enums.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PurchaseOrderService {
    PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);
    PurchaseOrderDTO getPurchaseOrderById(UUID id);
    List<PurchaseOrderDTO> getAllPurchaseOrders();
    PurchaseOrderDTO updatePurchaseOrder(UUID id, PurchaseOrderDTO purchaseOrderDTO);
    void deletePurchaseOrder(UUID id);
    List<PurchaseOrderDTO> getPurchaseOrdersBySupplier(UUID supplierId);
    List<PurchaseOrderDTO> getPurchaseOrdersByStatus(PurchaseOrderStatus status);

    List<PurchaseOrderDTO> getPurchaseOrdersByUser(UUID userId);

    PurchaseOrderDTO updatePurchaseOrderStatus(UUID id, PurchaseOrderStatus status);
    PurchaseOrderDTO approvePurchaseOrder(UUID id, UUID approvedByUserId);
    BigDecimal calculateOrderTotal(UUID purchaseOrderId);
}