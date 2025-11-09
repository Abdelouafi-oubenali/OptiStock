package com.example.demo.service.impl;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.User;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SalesOrderDTO createSalesOrder(SalesOrderDTO salesOrderDTO) {
        // Vérifier si l'utilisateur existe
        User user = userRepository.findById(salesOrderDTO.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + salesOrderDTO.getUser_id()));

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setUser(user);
        salesOrder.setOrderStatus(salesOrderDTO.getOrderStatus());

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
        return convertToDTO(savedOrder);
    }

    @Override
    public SalesOrderDTO getSalesOrderById(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + id));
        return convertToDTO(salesOrder);
    }

    @Override
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesOrderDTO updateSalesOrder(UUID id, SalesOrderDTO salesOrderDTO) {
        SalesOrder existingOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + id));

        existingOrder.setOrderStatus(salesOrderDTO.getOrderStatus());

        if (salesOrderDTO.getUser_id() != null) {
            User user = userRepository.findById(salesOrderDTO.getUser_id())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + salesOrderDTO.getUser_id()));
            existingOrder.setUser(user);
        }

        SalesOrder updatedOrder = salesOrderRepository.save(existingOrder);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteSalesOrder(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + id));
        salesOrderRepository.delete(salesOrder);
    }

    private SalesOrderDTO convertToDTO(SalesOrder salesOrder) {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setId(salesOrder.getId());
        dto.setUser_id(salesOrder.getUser().getId());
        dto.setOrderStatus(salesOrder.getOrderStatus());
        return dto;
    }
}