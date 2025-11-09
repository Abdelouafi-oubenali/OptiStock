package com.example.demo.controller;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentDTO> createShipment(@Valid @RequestBody ShipmentDTO shipmentDTO) {
        ShipmentDTO createdShipment = shipmentService.createShipment(shipmentDTO);
        return ResponseEntity.ok(createdShipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable UUID id) {
        ShipmentDTO shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    @GetMapping
    public ResponseEntity<List<ShipmentDTO>> getAllShipments() {
        List<ShipmentDTO> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipmentDTO> updateShipment(@PathVariable UUID id, @Valid @RequestBody ShipmentDTO shipmentDTO) {
        ShipmentDTO updatedShipment = shipmentService.updateShipment(id, shipmentDTO);
        return ResponseEntity.ok(updatedShipment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable UUID id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{salesOrderId}")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByOrder(@PathVariable UUID salesOrderId) {
        List<ShipmentDTO> shipments = shipmentService.getShipmentsByOrder(salesOrderId);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByCarrier(@PathVariable UUID carrierId) {
        List<ShipmentDTO> shipments = shipmentService.getShipmentsByCarrier(carrierId);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByStatus(@PathVariable ShipmentStatus status) {
        List<ShipmentDTO> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(shipments);
    }

    @PostMapping("/updateStatus/{id}")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable UUID id,
            @RequestBody ShipmentStatus status) {

        ShipmentDTO updatedShipment = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(updatedShipment);
    }



}