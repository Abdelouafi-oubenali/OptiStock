package com.example.demo.controller;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable UUID id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<SupplierDTO>> getActiveSuppliers() {
        List<SupplierDTO> activeSuppliers = supplierService.getActiveSuppliers();
        return ResponseEntity.ok(activeSuppliers);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<SupplierDTO>> getInactiveSuppliers() {
        List<SupplierDTO> inactiveSuppliers = supplierService.getInactiveSuppliers();
        return ResponseEntity.ok(inactiveSuppliers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliersByName(@RequestParam String name) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliersByName(name);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> checkSupplierExists(@PathVariable String name) {
        boolean exists = supplierService.supplierExists(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierDTO> getSupplierByName(@PathVariable String name) {
        SupplierDTO supplier = supplierService.getSupplierByName(name);
        return ResponseEntity.ok(supplier);
    }
}