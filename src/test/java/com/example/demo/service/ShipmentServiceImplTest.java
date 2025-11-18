package com.example.demo.service.impl;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.entity.Carrier;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.Shipment;
import com.example.demo.repository.CarrierRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private CarrierRepository carrierRepository;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    @Test
    void getShipmentById_ShouldReturnShipment() {
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = new Shipment();
        shipment.setId(shipmentId);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));

        ShipmentDTO result = shipmentService.getShipmentById(shipmentId);

        assertNotNull(result);
        assertEquals(shipmentId, result.getId());
    }

    @Test
    void getShipmentById_WhenNotFound_ShouldThrowException() {
        UUID shipmentId = UUID.randomUUID();
        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> shipmentService.getShipmentById(shipmentId));
    }

    @Test
    void deleteShipment_ShouldDeleteShipment() {
        UUID shipmentId = UUID.randomUUID();
        Shipment shipment = new Shipment();
        shipment.setId(shipmentId);

        when(shipmentRepository.findById(shipmentId)).thenReturn(Optional.of(shipment));
        doNothing().when(shipmentRepository).delete(shipment);

        shipmentService.deleteShipment(shipmentId);

        verify(shipmentRepository, times(1)).delete(shipment);
    }
}