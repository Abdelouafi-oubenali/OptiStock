package com.example.demo.controller;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Validated
public class WarehouseController {

    private final WarehouseService warehouseService;


    @PostMapping
    public ResponseEntity<?> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) {
        try {
            WarehouseDTO created = warehouseService.createWarehouse(warehouseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable UUID id) {
        try {
            WarehouseDTO warehouse = warehouseService.getWarehouseById(id);
            return ResponseEntity.ok(warehouse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable UUID id,
                                             @Valid @RequestBody WarehouseDTO warehouseDTO) {
        try {
            WarehouseDTO updated = warehouseService.updateWarehouse(id, warehouseDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable UUID id) {
        try {
            warehouseService.deleteWarehouse(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<WarehouseDTO>> getActiveWarehouses() {
        List<WarehouseDTO> activeWarehouses = warehouseService.getActiveWarehouses();
        return ResponseEntity.ok(activeWarehouses);
    }
}