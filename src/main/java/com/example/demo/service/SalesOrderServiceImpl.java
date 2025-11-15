package com.example.demo.service.impl;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.User;
import com.example.demo.mapper.SalesOrderMapper;
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

    private final SalesOrderMapper mapper = SalesOrderMapper.INSTANCE;

    @Override
    @Transactional
    public SalesOrderDTO createSalesOrder(SalesOrderDTO dto) {
        User user = userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUser_id()));

        SalesOrder salesOrder = mapper.toEntity(dto);
        salesOrder.setUser(user);

        return mapper.toDTO(salesOrderRepository.save(salesOrder));
    }

    @Override
    public SalesOrderDTO getSalesOrderById(UUID id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + id));
        return mapper.toDTO(order);
    }

    @Override
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesOrderDTO updateSalesOrder(UUID id, SalesOrderDTO dto) {
        SalesOrder existingOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + id));

        existingOrder.setOrderStatus(dto.getOrderStatus());

        if (dto.getUser_id() != null) {
            User user = userRepository.findById(dto.getUser_id())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUser_id()));
            existingOrder.setUser(user);
        }

        return mapper.toDTO(salesOrderRepository.save(existingOrder));
    }

    @Override
    @Transactional
    public void deleteSalesOrder(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + id));
        salesOrderRepository.delete(salesOrder);
    }
}
