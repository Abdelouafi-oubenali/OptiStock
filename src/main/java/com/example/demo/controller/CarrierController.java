package com.example.demo.controller;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carriers")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping
    public ResponseEntity<CarrierDTO> createCarrier(@Valid @RequestBody CarrierDTO carrierDTO) {
        CarrierDTO createdCarrier = carrierService.createCarrier(carrierDTO);
        return ResponseEntity.ok(createdCarrier);
    }

    @GetMapping
    public ResponseEntity<List<CarrierDTO>> getAllCarriers() {
        List<CarrierDTO> carriers = carrierService.getAllCarriers();
        return ResponseEntity.ok(carriers);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CarrierDTO>> getActiveCarriers() {
        List<CarrierDTO> carriers = carrierService.getActiveCarriers();
        return ResponseEntity.ok(carriers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierDTO> getCarrierById(@PathVariable UUID id) {
        CarrierDTO carrier = carrierService.getCarrierById(id);
        return ResponseEntity.ok(carrier);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CarrierDTO> getCarrierByName(@PathVariable String name) {
        CarrierDTO carrier = carrierService.getCarrierByName(name);
        return ResponseEntity.ok(carrier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarrierDTO> updateCarrier(@PathVariable UUID id, @Valid @RequestBody CarrierDTO carrierDTO) {
        CarrierDTO updatedCarrier = carrierService.updateCarrier(id, carrierDTO);
        return ResponseEntity.ok(updatedCarrier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrier(@PathVariable UUID id) {
        carrierService.deleteCarrier(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCarrier(@PathVariable UUID id) {
        carrierService.deactivateCarrier(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCarrier(@PathVariable UUID id) {
        carrierService.activateCarrier(id);
        return ResponseEntity.noContent().build();
    }
}