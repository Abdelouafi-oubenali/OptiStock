package com.example.demo.service;

import com.example.demo.dto.SalesOrderLineDTO;
import java.util.List;
import java.util.UUID;

public interface SalesOrderLineService {
    SalesOrderLineDTO createSalesOrderLine(SalesOrderLineDTO salesOrderLineDTO);
    SalesOrderLineDTO getSalesOrderLineById(UUID id);
    List<SalesOrderLineDTO> getAllSalesOrderLines();
    List<SalesOrderLineDTO> getSalesOrderLinesByOrder(UUID salesOrderId);
    List<SalesOrderLineDTO> getSalesOrderLinesByProduct(UUID productId);
    SalesOrderLineDTO updateSalesOrderLine(UUID id, SalesOrderLineDTO salesOrderLineDTO);
    void deleteSalesOrderLine(UUID id);
    void deleteSalesOrderLinesByOrder(UUID salesOrderId);
}