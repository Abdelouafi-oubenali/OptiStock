package com.example.demo.service;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.entity.Warehouse;
import com.example.demo.mapper.InventoryMapper;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImpTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImp inventoryService;

    private Inventory inventory;
    private InventoryDTO inventoryDTO;
    private Warehouse warehouse;
    private Product product;
    private UUID inventoryId;
    private UUID warehouseId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        inventoryId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
        productId = UUID.randomUUID();

        warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setName("Main Warehouse");

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        inventory = new Inventory();
        inventory.setId(inventoryId);
        inventory.setQtyOnHand(100);
        inventory.setQtyReserved(10);
        inventory.setReferenceDocument("REF-001");
        inventory.setWarehouse(warehouse);
        inventory.setProduct(product);

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(inventoryId);
        inventoryDTO.setQtyOnHand(100);
        inventoryDTO.setQtyReserved(10);
        inventoryDTO.setReferenceDocument("REF-001");
        inventoryDTO.setWarehouse_id(warehouseId);
        inventoryDTO.setProduct_id(productId);
    }

    @Test
    void createInventory_ShouldReturnSavedInventory() {
        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
                .thenReturn(Optional.empty());
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = inventoryService.createInventory(inventoryDTO);

        assertNotNull(result);
        assertEquals(inventoryDTO.getQtyOnHand(), result.getQtyOnHand());
        assertEquals(inventoryDTO.getQtyReserved(), result.getQtyReserved());
        verify(inventoryRepository, times(1)).findByProductIdAndWarehouseId(productId, warehouseId);
        verify(warehouseRepository, times(1)).findById(warehouseId);
        verify(productRepository, times(1)).findById(productId);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void createInventory_WhenInventoryAlreadyExists_ShouldThrowException() {
        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
                .thenReturn(Optional.of(inventory));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.createInventory(inventoryDTO));

        assertEquals("Un inventory pour ce produit dans ce warehouse existe déjà !", exception.getMessage());
        verify(inventoryRepository, times(1)).findByProductIdAndWarehouseId(productId, warehouseId);
        verify(warehouseRepository, never()).findById(any());
        verify(productRepository, never()).findById(any());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void createInventory_WhenWarehouseNotFound_ShouldThrowException() {
        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
                .thenReturn(Optional.empty());
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.createInventory(inventoryDTO));

        assertEquals("Warehouse non trouvé", exception.getMessage());
        verify(warehouseRepository, times(1)).findById(warehouseId);
        verify(productRepository, never()).findById(any());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void createInventory_WhenProductNotFound_ShouldThrowException() {
        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
                .thenReturn(Optional.empty());
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.createInventory(inventoryDTO));

        assertEquals("Product non trouvé", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void getInventoryById_ShouldReturnInventory() {
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));

        InventoryDTO result = inventoryService.getInventoryById(inventoryId);

        assertNotNull(result);
        assertEquals(inventoryId, result.getId());
        assertEquals(inventory.getQtyOnHand(), result.getQtyOnHand());
        verify(inventoryRepository, times(1)).findById(inventoryId);
    }

    @Test
    void getInventoryById_WhenNotFound_ShouldThrowException() {
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.getInventoryById(inventoryId));

        assertEquals("Inventory non trouvé avec l'id: " + inventoryId, exception.getMessage());
        verify(inventoryRepository, times(1)).findById(inventoryId);
    }

    @Test
    void getAllInventories_ShouldReturnAllInventories() {
        List<Inventory> inventories = Arrays.asList(inventory, createAnotherInventory());
        when(inventoryRepository.findAll()).thenReturn(inventories);

        List<InventoryDTO> result = inventoryService.getAllInventories();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(inventoryRepository, times(1)).findAll();
    }

//    @Test
//    void updateInventory_ShouldUpdateAndReturnInventory() {
//        // Arrange
//        InventoryDTO updateDTO = new InventoryDTO();
//        updateDTO.setQtyOnHand(150);
//        updateDTO.setQtyReserved(20);
//        updateDTO.setReferenceDocument("REF-002");
//        updateDTO.setWarehouse_id(warehouseId);
//        updateDTO.setProduct_id(productId);
//
//        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
//                .thenReturn(Optional.empty());
//        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
//        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
//
//        InventoryDTO result = inventoryService.updateInventory(inventoryId, updateDTO);
//
//        assertNotNull(result);
//        verify(inventoryRepository, times(1)).findByProductIdAndWarehouseId(productId, warehouseId);
//        verify(inventoryRepository, times(1)).findById(inventoryId);
//        verify(warehouseRepository, times(1)).findById(warehouseId);
//        verify(productRepository, times(1)).findById(productId);
//        verify(inventoryRepository, times(1)).save(inventory);
//    }

    //@Test
//    void updateInventory_WhenInventoryAlreadyExists_ShouldThrowException() {
//        Inventory existingInventoryWithSameProductWarehouse = new Inventory();
//        existingInventoryWithSameProductWarehouse.setId(UUID.randomUUID()); // Different ID
//
//        InventoryDTO updateDTO = new InventoryDTO();
//        updateDTO.setWarehouse_id(warehouseId);
//        updateDTO.setProduct_id(productId);
//
//        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
//                .thenReturn(Optional.of(existingInventoryWithSameProductWarehouse));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> inventoryService.updateInventory(inventoryId, updateDTO));
//
//        assertEquals("Un inventory pour ce produit dans ce warehouse existe déjà !", exception.getMessage());
//        verify(inventoryRepository, times(1)).findByProductIdAndWarehouseId(productId, warehouseId);
//        verify(inventoryRepository, never()).findById(any());
//        verify(inventoryRepository, never()).save(any(Inventory.class));
//    }

//    @Test
//    void updateInventory_WhenInventoryNotFound_ShouldThrowException() {
//        // Arrange
//        InventoryDTO updateDTO = new InventoryDTO();
//        updateDTO.setWarehouse_id(warehouseId);
//        updateDTO.setProduct_id(productId);
//
//        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
//                .thenReturn(Optional.empty());
//        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> inventoryService.updateInventory(inventoryId, updateDTO));
//
//        assertEquals("Inventory non trouvé avec l'id: " + inventoryId, exception.getMessage());
//        verify(inventoryRepository, times(1)).findById(inventoryId);
//        verify(inventoryRepository, never()).save(any(Inventory.class));
//    }

    @Test
    void updateInventory_WhenWarehouseNotFound_ShouldThrowException() {
        // Arrange
        InventoryDTO updateDTO = new InventoryDTO();
        updateDTO.setWarehouse_id(warehouseId);
        updateDTO.setProduct_id(productId);

        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
                .thenReturn(Optional.empty());
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.updateInventory(inventoryId, updateDTO));

        assertEquals("Warehouse non trouvé", exception.getMessage());
        verify(warehouseRepository, times(1)).findById(warehouseId);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void updateInventory_WhenProductNotFound_ShouldThrowException() {
        InventoryDTO updateDTO = new InventoryDTO();
        updateDTO.setWarehouse_id(warehouseId);
        updateDTO.setProduct_id(productId);

        when(inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId))
                .thenReturn(Optional.empty());
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.updateInventory(inventoryId, updateDTO));

        assertEquals("Product non trouvé", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void deleteInventory_ShouldDeleteInventory() {
        // Arrange
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        doNothing().when(inventoryRepository).delete(inventory);

        // Act
        inventoryService.deleteInventory(inventoryId);

        // Assert
        verify(inventoryRepository, times(1)).findById(inventoryId);
        verify(inventoryRepository, times(1)).delete(inventory);
    }

    @Test
    void deleteInventory_WhenNotFound_ShouldThrowException() {
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.deleteInventory(inventoryId));

        assertEquals("Inventory non trouvé avec l'id: " + inventoryId, exception.getMessage());
        verify(inventoryRepository, times(1)).findById(inventoryId);
        verify(inventoryRepository, never()).delete(any(Inventory.class));
    }

    private Inventory createAnotherInventory() {
        Inventory anotherInventory = new Inventory();
        anotherInventory.setId(UUID.randomUUID());
        anotherInventory.setQtyOnHand(200);
        anotherInventory.setQtyReserved(15);
        anotherInventory.setReferenceDocument("REF-002");

        Warehouse anotherWarehouse = new Warehouse();
        anotherWarehouse.setId(UUID.randomUUID());
        anotherWarehouse.setName("Secondary Warehouse");

        Product anotherProduct = new Product();
        anotherProduct.setId(UUID.randomUUID());
        anotherProduct.setName("Another Product");

        anotherInventory.setWarehouse(anotherWarehouse);
        anotherInventory.setProduct(anotherProduct);

        return anotherInventory;
    }
}