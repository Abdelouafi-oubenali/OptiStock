package com.example.demo.service;

import com.example.demo.dto.WarehouseDTO;
import java.util.List;
import java.util.UUID;

public interface WarehouseService {
    WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO);
    List<WarehouseDTO> getAllWarehouses();
    WarehouseDTO getWarehouseById(UUID id);
    WarehouseDTO updateWarehouse(UUID id, WarehouseDTO warehouseDTO);
    void deleteWarehouse(UUID id);
    List<WarehouseDTO> getActiveWarehouses();
}
