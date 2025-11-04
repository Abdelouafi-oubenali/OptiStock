package com.example.demo.service;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Warehouse;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.WarehouseRepository;
import com.example.demo.repository.ProductRepository;
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

    @Override
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        Optional<Inventory> existingInventory = inventoryRepository
                .findByProductIdAndWarehouseId(inventoryDTO.getProduct_id(), inventoryDTO.getWarehouse_id()) ;
        if (existingInventory.isPresent()) {
            throw new RuntimeException("Un inventory pour ce produit dans ce warehouse existe déjà !");
        }

        Inventory inventory = toEntity(inventoryDTO);

        Inventory saved = inventoryRepository.save(inventory);
        return toDto(saved);
    }

    @Override
    public InventoryDTO getInventoryById(UUID id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory non trouvé avec l'id: " + id));
        return toDto(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO updateInventory(UUID id, InventoryDTO inventoryDTO) {

        Optional<Inventory> existingInventoryProducts = inventoryRepository
                .findByProductIdAndWarehouseId(inventoryDTO.getProduct_id(), inventoryDTO.getWarehouse_id()) ;
        if (existingInventoryProducts.isPresent()) {
            throw new RuntimeException("Un inventory pour ce produit dans ce warehouse existe déjà !");
        }

        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory non trouvé avec l'id: " + id));

        // Charger Warehouse et Product depuis les IDs
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
        return toDto(updated);
    }

    @Override
    public void deleteInventory(UUID id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory non trouvé avec l'id: " + id));
        inventoryRepository.delete(inventory);
    }

    // Méthodes utilitaires de conversion CORRIGÉES
    private Inventory toEntity(InventoryDTO dto) {
        Inventory inventory = new Inventory();
        inventory.setId(dto.getId());
        inventory.setQtyOnHand(dto.getQtyOnHand());
        inventory.setQtyReserved(dto.getQtyReserved());
        inventory.setReferenceDocument(dto.getReferenceDocument());

        // CHARGER LES ENTITÉS Warehouse ET Product
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouse_id())
                .orElseThrow(() -> new RuntimeException("Warehouse non trouvé avec l'id: " + dto.getWarehouse_id()));
        Product product = productRepository.findById(dto.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Product non trouvé avec l'id: " + dto.getProduct_id()));

        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);

        return inventory;
    }

    private InventoryDTO toDto(Inventory entity) {
        return InventoryDTO.builder()
                .id(entity.getId())
                .qtyOnHand(entity.getQtyOnHand())
                .qtyReserved(entity.getQtyReserved())
                .referenceDocument(entity.getReferenceDocument())
                .warehouse_id(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .product_id(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .build();
    }
}