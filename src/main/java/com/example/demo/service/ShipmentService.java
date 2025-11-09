package com.example.demo.service;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.ShipmentStatus;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    ShipmentDTO createShipment(ShipmentDTO shipmentDTO);
    ShipmentDTO getShipmentById(UUID id);
    List<ShipmentDTO> getAllShipments();
    ShipmentDTO updateShipment(UUID id, ShipmentDTO shipmentDTO);
    void deleteShipment(UUID id);
    List<ShipmentDTO> getShipmentsByOrder(UUID salesOrderId);
    List<ShipmentDTO> getShipmentsByCarrier(UUID carrierId);
    List<ShipmentDTO> getShipmentsByStatus(ShipmentStatus status);
     ShipmentDTO updateShipmentStatus(UUID id, ShipmentStatus status) ;
}