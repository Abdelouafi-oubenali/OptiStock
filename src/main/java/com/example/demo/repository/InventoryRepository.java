package com.example.demo.repository;

import com.example.demo.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByWarehouseId(UUID warehouseId);
    Inventory findByProductId(UUID productId);
    List<Inventory> findByQtyOnHandGreaterThan(Integer qty);
    Optional<Inventory> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);

}
