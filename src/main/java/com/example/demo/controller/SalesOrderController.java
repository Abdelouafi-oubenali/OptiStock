package com.example.demo.controller;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrderDTO> createSalesOrder(@Valid @RequestBody SalesOrderDTO salesOrderDTO) {
        SalesOrderDTO createdOrder = salesOrderService.createSalesOrder(salesOrderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderDTO> getSalesOrderById(@PathVariable UUID id) {
        SalesOrderDTO salesOrder = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(salesOrder);
    }

    @GetMapping
    public ResponseEntity<List<SalesOrderDTO>> getAllSalesOrders() {
        List<SalesOrderDTO> salesOrders = salesOrderService.getAllSalesOrders();
        return ResponseEntity.ok(salesOrders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesOrderDTO> updateSalesOrder(
            @PathVariable UUID id,
            @Valid @RequestBody SalesOrderDTO salesOrderDTO) {
        SalesOrderDTO updatedOrder = salesOrderService.updateSalesOrder(id, salesOrderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesOrder(@PathVariable UUID id) {
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}