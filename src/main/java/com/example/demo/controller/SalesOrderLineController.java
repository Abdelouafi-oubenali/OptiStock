package com.example.demo.controller;

import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.service.SalesOrderLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales-order-lines")
@RequiredArgsConstructor
public class SalesOrderLineController {

    private final SalesOrderLineService salesOrderLineService;

    @PostMapping
    public ResponseEntity<SalesOrderLineDTO> createSalesOrderLine(@Valid @RequestBody SalesOrderLineDTO salesOrderLineDTO) {
        SalesOrderLineDTO createdLine = salesOrderLineService.createSalesOrderLine(salesOrderLineDTO);
        return new ResponseEntity<>(createdLine, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderLineDTO> getSalesOrderLineById(@PathVariable UUID id) {
        SalesOrderLineDTO salesOrderLine = salesOrderLineService.getSalesOrderLineById(id);
        return ResponseEntity.ok(salesOrderLine);
    }

    @GetMapping
    public ResponseEntity<List<SalesOrderLineDTO>> getAllSalesOrderLines() {
        List<SalesOrderLineDTO> salesOrderLines = salesOrderLineService.getAllSalesOrderLines();
        return ResponseEntity.ok(salesOrderLines);
    }

    @GetMapping("/order/{salesOrderId}")
    public ResponseEntity<List<SalesOrderLineDTO>> getSalesOrderLinesByOrder(@PathVariable UUID salesOrderId) {
        List<SalesOrderLineDTO> salesOrderLines = salesOrderLineService.getSalesOrderLinesByOrder(salesOrderId);
        return ResponseEntity.ok(salesOrderLines);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SalesOrderLineDTO>> getSalesOrderLinesByProduct(@PathVariable UUID productId) {
        List<SalesOrderLineDTO> salesOrderLines = salesOrderLineService.getSalesOrderLinesByProduct(productId);
        return ResponseEntity.ok(salesOrderLines);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesOrderLineDTO> updateSalesOrderLine(
            @PathVariable UUID id,
            @Valid @RequestBody SalesOrderLineDTO salesOrderLineDTO) {
        SalesOrderLineDTO updatedLine = salesOrderLineService.updateSalesOrderLine(id, salesOrderLineDTO);
        return ResponseEntity.ok(updatedLine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesOrderLine(@PathVariable UUID id) {
        salesOrderLineService.deleteSalesOrderLine(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/order/{salesOrderId}")
    public ResponseEntity<Void> deleteSalesOrderLinesByOrder(@PathVariable UUID salesOrderId) {
        salesOrderLineService.deleteSalesOrderLinesByOrder(salesOrderId);
        return ResponseEntity.noContent().build();
    }
}