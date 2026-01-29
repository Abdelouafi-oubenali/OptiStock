package com.example.demo.service.impl;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.Shipment;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.mapper.ShipmentMapper;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final SalesOrderRepository salesOrderRepository;

    private final ShipmentMapper mapper = ShipmentMapper.INSTANCE;

    private LocalDateTime adjustDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (dateTime.isBefore(now)) {
            throw new RuntimeException("La date ne peut pas être dans le passé.");
        }
        if (dateTime.toLocalTime().isAfter(LocalTime.of(15, 0))) {
            return dateTime.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        }
        return dateTime;
    }

    @Override
    @Transactional
    public ShipmentDTO createShipment(ShipmentDTO dto) {
        if (shipmentRepository.existsByTrackingNumber(dto.getTrackingNumber())) {
            throw new RuntimeException("Le numéro de suivi existe déjà : " + dto.getTrackingNumber());
        }

        SalesOrder salesOrder = salesOrderRepository.findById(dto.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id : " + dto.getSalesOrderId()));

        Shipment shipment = mapper.toEntity(dto);
        shipment.setSalesOrder(salesOrder);

        shipment.setPlannedDate(adjustDate(dto.getPlannedDate()));
        shipment.setShippedDate(adjustDate(dto.getShippedDate()));
        shipment.setDeliveredDate(adjustDate(dto.getDeliveredDate()));

        return mapper.toDTO(shipmentRepository.save(shipment));
    }

    @Override
    public ShipmentDTO getShipmentById(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
        return mapper.toDTO(shipment);
    }

    @Override
    public List<ShipmentDTO> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShipmentDTO updateShipment(UUID id, ShipmentDTO dto) {
        Shipment existing = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expédition introuvable avec l'id: " + id));

        if (!existing.getTrackingNumber().equals(dto.getTrackingNumber()) &&
                shipmentRepository.existsByTrackingNumber(dto.getTrackingNumber())) {
            throw new RuntimeException("Le numéro de suivi existe déjà : " + dto.getTrackingNumber());
        }

        existing.setTrackingNumber(dto.getTrackingNumber());
        existing.setStatus(dto.getStatus());
        existing.setPlannedDate(adjustDate(dto.getPlannedDate()));
        existing.setShippedDate(adjustDate(dto.getShippedDate()));
        existing.setDeliveredDate(adjustDate(dto.getDeliveredDate()));

        if (dto.getSalesOrderId() != null) {
            SalesOrder salesOrder = salesOrderRepository.findById(dto.getSalesOrderId())
                    .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id: " + dto.getSalesOrderId()));
            existing.setSalesOrder(salesOrder);
        }

        return mapper.toDTO(shipmentRepository.save(existing));
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
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipmentDTO> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShipmentDTO updateShipmentStatus(UUID id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus(status);
        return mapper.toDTO(shipmentRepository.save(shipment));
    }
}
