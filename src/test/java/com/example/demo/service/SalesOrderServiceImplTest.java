package com.example.demo.service.impl;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.User;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.UserRepository;
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
class SalesOrderServiceImplTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SalesOrderServiceImpl salesOrderService;

    @Test
    void createSalesOrder_ShouldReturnSavedSalesOrder() {
        UUID userId = UUID.randomUUID();
        UUID salesOrderId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(salesOrderId);

        SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setUser_id(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);

        SalesOrderDTO result = salesOrderService.createSalesOrder(salesOrderDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
    }

    @Test
    void createSalesOrder_WhenUserNotFound_ShouldThrowException() {
        UUID userId = UUID.randomUUID();
        SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setUser_id(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> salesOrderService.createSalesOrder(salesOrderDTO));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(salesOrderRepository, never()).save(any(SalesOrder.class));
    }

    @Test
    void getSalesOrderById_ShouldReturnSalesOrder() {
        UUID salesOrderId = UUID.randomUUID();
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(salesOrderId);

        when(salesOrderRepository.findById(salesOrderId)).thenReturn(Optional.of(salesOrder));

        SalesOrderDTO result = salesOrderService.getSalesOrderById(salesOrderId);

        assertNotNull(result);
        assertEquals(salesOrderId, result.getId());
    }

    @Test
    void getSalesOrderById_WhenNotFound_ShouldThrowException() {
        UUID salesOrderId = UUID.randomUUID();
        when(salesOrderRepository.findById(salesOrderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> salesOrderService.getSalesOrderById(salesOrderId));

        assertEquals("SalesOrder not found with id: " + salesOrderId, exception.getMessage());
    }

    @Test
    void updateSalesOrder_ShouldUpdateAndReturnSalesOrder() {
        UUID salesOrderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SalesOrder existingOrder = new SalesOrder();
        existingOrder.setId(salesOrderId);

        User user = new User();
        user.setId(userId);

        SalesOrderDTO updateDTO = new SalesOrderDTO();
        updateDTO.setUser_id(userId);

        when(salesOrderRepository.findById(salesOrderId)).thenReturn(Optional.of(existingOrder));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(existingOrder);

        SalesOrderDTO result = salesOrderService.updateSalesOrder(salesOrderId, updateDTO);

        assertNotNull(result);
        verify(salesOrderRepository, times(1)).save(existingOrder);
    }

    @Test
    void deleteSalesOrder_ShouldDeleteSalesOrder() {
        UUID salesOrderId = UUID.randomUUID();
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(salesOrderId);

        when(salesOrderRepository.findById(salesOrderId)).thenReturn(Optional.of(salesOrder));
        doNothing().when(salesOrderRepository).delete(salesOrder);

        salesOrderService.deleteSalesOrder(salesOrderId);

        verify(salesOrderRepository, times(1)).delete(salesOrder);
    }
}