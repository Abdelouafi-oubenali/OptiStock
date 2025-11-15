package com.example.demo.service;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.entity.Warehouse;
import com.example.demo.mapper.InventoryMapper;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImp implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final InventoryMapper mapper = InventoryMapper.INSTANCE;

    @Override
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        Optional<Inventory> existingInventory = inventoryRepository
                .findByProductIdAndWarehouseId(inventoryDTO.getProduct_id(), inventoryDTO.getWarehouse_id());
        if (existingInventory.isPresent()) {
            throw new RuntimeException("Un inventory pour ce produit dans ce warehouse existe déjà !");
        }

        Inventory inventory = mapper.toEntity(inventoryDTO);

        Warehouse warehouse = warehouseRepository.findById(inventoryDTO.getWarehouse_id())
                .orElseThrow(() -> new RuntimeException("Warehouse non trouvé"));
        Product product = productRepository.findById(inventoryDTO.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Product non trouvé"));
        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);

        Inventory saved = inventoryRepository.save(inventory);
        return mapper.toDTO(saved);
    }

    @Override
    public InventoryDTO getInventoryById(UUID id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory non trouvé avec l'id: " + id));
        return mapper.toDTO(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO updateInventory(UUID id, InventoryDTO inventoryDTO) {

        Optional<Inventory> existingInventoryProducts = inventoryRepository
                .findByProductIdAndWarehouseId(inventoryDTO.getProduct_id(), inventoryDTO.getWarehouse_id());
        if (existingInventoryProducts.isPresent()) {
            throw new RuntimeException("Un inventory pour ce produit dans ce warehouse existe déjà !");
        }

        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory non trouvé avec l'id: " + id));

        Warehouse warehouse = warehouseRepository.findById(inventoryDTO.getWarehouse_id())
                .orElseThrow(() -> new RuntimeException("Warehouse non trouvé"));
        Product product = productRepository.findById(inventoryDTO.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Product non trouvé"));

        existingInventory.setQtyOnHand(inventoryDTO.getQtyOnHand());
        existingInventory.setQtyReserved(inventoryDTO.getQtyReserved());
        existingInventory.setReferenceDocument(inventoryDTO.getReferenceDocument());
        existingInventory.setWarehouse(warehouse);
        existingInventory.setProduct(product);

        Inventory updated = inventoryRepository.save(existingInventory);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteInventory(UUID id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory non trouvé avec l'id: " + id));
        inventoryRepository.delete(inventory);
    }
}
