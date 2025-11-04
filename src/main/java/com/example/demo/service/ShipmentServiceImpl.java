package com.example.demo.service.impl;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.entity.Shipment;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.Carrier;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.CarrierRepository;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final CarrierRepository carrierRepository;

    @Override
    @Transactional
    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO) {
        if (shipmentRepository.existsByTrackingNumber(shipmentDTO.getTrackingNumber())) {
            throw new RuntimeException("Le numéro de suivi existe déjà : " + shipmentDTO.getTrackingNumber());
        }

        SalesOrder salesOrder = salesOrderRepository.findById(shipmentDTO.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id : " + shipmentDTO.getSalesOrderId()));

        Carrier carrier = carrierRepository.findById(shipmentDTO.getCarrierId())
                .orElseThrow(() -> new RuntimeException("Transporteur introuvable avec l'id : " + shipmentDTO.getCarrierId()));

        LocalDateTime now = LocalDateTime.now();

        Consumer<LocalDateTime> checkDate = (dateTime) -> {
            if (dateTime != null) {
                if (dateTime.isBefore(now)) {
                    throw new RuntimeException("La date ne peut pas être dans le passé.");
                }
                if (dateTime.toLocalTime().isAfter(LocalTime.of(15, 0))) {
                    throw new RuntimeException("Impossible d’entrer une heure après 15:00, quel que soit le jour.");
                }
            }
        };

        checkDate.accept(shipmentDTO.getPlannedDate());
        checkDate.accept(shipmentDTO.getShippedDate());
        checkDate.accept(shipmentDTO.getDeliveredDate());

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(shipmentDTO.getTrackingNumber());
        shipment.setStatus(shipmentDTO.getStatus());
        shipment.setPlannedDate(shipmentDTO.getPlannedDate());
        shipment.setShippedDate(shipmentDTO.getShippedDate());
        shipment.setDeliveredDate(shipmentDTO.getDeliveredDate());
        shipment.setSalesOrder(salesOrder);
        shipment.setCarrier(carrier);

        Shipment savedShipment = shipmentRepository.save(shipment);
        return convertToDTO(savedShipment);
    }


    @Override
    public ShipmentDTO getShipmentById(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
        return convertToDTO(shipment);
    }

    @Override
    public List<ShipmentDTO> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShipmentDTO updateShipment(UUID id, ShipmentDTO shipmentDTO) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expédition introuvable avec l'id: " + id));

        if (!existingShipment.getTrackingNumber().equals(shipmentDTO.getTrackingNumber()) &&
                shipmentRepository.existsByTrackingNumber(shipmentDTO.getTrackingNumber())) {
            throw new RuntimeException("Le numéro de suivi existe déjà : " + shipmentDTO.getTrackingNumber());
        }

        LocalDateTime now = LocalDateTime.now();

        Consumer<LocalDateTime> checkDate = (dateTime) -> {
            if (dateTime != null) {
                if (dateTime.isBefore(now)) {
                    throw new RuntimeException("La date ne peut pas être dans le passé.");
                }
                if (dateTime.toLocalTime().isAfter(LocalTime.of(15, 0))) {
                    throw new RuntimeException("Impossible d’entrer une heure après 15:00, quel que soit le jour.");
                }
            }
        };

        checkDate.accept(shipmentDTO.getPlannedDate());
        checkDate.accept(shipmentDTO.getShippedDate());
        checkDate.accept(shipmentDTO.getDeliveredDate());

        existingShipment.setTrackingNumber(shipmentDTO.getTrackingNumber());
        existingShipment.setStatus(shipmentDTO.getStatus());
        existingShipment.setPlannedDate(shipmentDTO.getPlannedDate());
        existingShipment.setShippedDate(shipmentDTO.getShippedDate());
        existingShipment.setDeliveredDate(shipmentDTO.getDeliveredDate());

        if (shipmentDTO.getSalesOrderId() != null) {
            SalesOrder salesOrder = salesOrderRepository.findById(shipmentDTO.getSalesOrderId())
                    .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id: " + shipmentDTO.getSalesOrderId()));
            existingShipment.setSalesOrder(salesOrder);
        }

        if (shipmentDTO.getCarrierId() != null) {
            Carrier carrier = carrierRepository.findById(shipmentDTO.getCarrierId())
                    .orElseThrow(() -> new RuntimeException("Transporteur introuvable avec l'id: " + shipmentDTO.getCarrierId()));
            existingShipment.setCarrier(carrier);
        }

        Shipment updatedShipment = shipmentRepository.save(existingShipment);
        return convertToDTO(updatedShipment);
    }


    @Override
    @Transactional
    public void deleteShipment(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
        shipmentRepository.delete(shipment);
    }

    @Override
    public List<ShipmentDTO> getShipmentsByOrder(UUID salesOrderId) {
        return shipmentRepository.findBySalesOrderId(salesOrderId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentDTO> getShipmentsByCarrier(UUID carrierId) {
        return shipmentRepository.findByCarrierId(carrierId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentDTO> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ShipmentDTO convertToDTO(Shipment shipment) {
        return ShipmentDTO.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus())
                .plannedDate(shipment.getPlannedDate())
                .shippedDate(shipment.getShippedDate())
                .deliveredDate(shipment.getDeliveredDate())
                .salesOrderId(shipment.getSalesOrder().getId())
                .carrierId(shipment.getCarrier().getId())
                .build();
    }
}