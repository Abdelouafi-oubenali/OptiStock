package com.example.demo.service;

import com.example.demo.dto.SalesOrderDTO;
import java.util.List;
import java.util.UUID;

public interface SalesOrderService {
    SalesOrderDTO createSalesOrder(SalesOrderDTO salesOrderDTO);
    SalesOrderDTO getSalesOrderById(UUID id);
    List<SalesOrderDTO> getAllSalesOrders();
    SalesOrderDTO updateSalesOrder(UUID id, SalesOrderDTO salesOrderDTO);
    void deleteSalesOrder(UUID id);
}