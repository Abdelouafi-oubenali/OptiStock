package com.example.demo.controller;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrderDTO createdOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders() {
        List<PurchaseOrderDTO> orders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getPurchaseOrderById(@PathVariable UUID id) {
        PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrder(@PathVariable UUID id, @Valid @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrderDTO updatedOrder = purchaseOrderService.updatePurchaseOrder(id, purchaseOrderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable UUID id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderDTO>> getPurchaseOrdersBySupplier(@PathVariable UUID supplierId) {
        List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersBySupplier(supplierId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrderDTO>> getPurchaseOrdersByStatus(@PathVariable PurchaseOrderStatus status) {
        List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // CORRECTION : Ajouter cet endpoint
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseOrderDTO>> getPurchaseOrdersByUser(@PathVariable UUID userId) {
        List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrderStatus(@PathVariable UUID id, @PathVariable PurchaseOrderStatus status) {
        PurchaseOrderDTO updatedOrder = purchaseOrderService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/approve/{approvedByUserId}")
    public ResponseEntity<PurchaseOrderDTO> approvePurchaseOrder(@PathVariable UUID id, @PathVariable UUID approvedByUserId) {
        PurchaseOrderDTO approvedOrder = purchaseOrderService.approvePurchaseOrder(id, approvedByUserId);
        return ResponseEntity.ok(approvedOrder);
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<BigDecimal> getOrderTotal(@PathVariable UUID id) {
        BigDecimal total = purchaseOrderService.calculateOrderTotal(id);
        return ResponseEntity.ok(total);
    }
}