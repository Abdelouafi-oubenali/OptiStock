package com.example.demo.repository;

import com.example.demo.entity.Shipment;
import com.example.demo.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    List<Shipment> findBySalesOrderId(UUID salesOrderId);
    List<Shipment> findByStatus(ShipmentStatus status);
    boolean existsByTrackingNumber(String trackingNumber);
}