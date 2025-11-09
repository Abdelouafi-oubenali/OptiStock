package com.example.demo.service;

import com.example.demo.dto.InventoryDTO;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    InventoryDTO createInventory(InventoryDTO inventoryDTO);
    InventoryDTO getInventoryById(UUID id);
    List<InventoryDTO> getAllInventories();
    InventoryDTO updateInventory(UUID id, InventoryDTO inventoryDTO);
    void deleteInventory(UUID id);
}