package com.example.demo.service;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.entity.Warehouse;
import com.example.demo.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImp implements  WarehouseService{

    private final WarehouseRepository warehouseRepository;

    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        if (warehouseRepository.existsByName(warehouseDTO.getName())) {
            throw new RuntimeException("Un entrepôt avec ce nom existe déjà");
        }

        Warehouse warehouse = toEntity(warehouseDTO);
        Warehouse saved = warehouseRepository.save(warehouse);
        return toDTO(saved);
    }

    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WarehouseDTO getWarehouseById(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));
        return toDTO(warehouse);
    }

    public WarehouseDTO updateWarehouse(UUID id, WarehouseDTO warehouseDTO) {
        Warehouse existingWarehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));

        if (!existingWarehouse.getName().equals(warehouseDTO.getName()) &&
                warehouseRepository.existsByName(warehouseDTO.getName())) {
            throw new RuntimeException("Un entrepôt avec ce nom existe déjà");
        }

        existingWarehouse.setName(warehouseDTO.getName());
        existingWarehouse.setVille(warehouseDTO.getVille());
        existingWarehouse.setActive(warehouseDTO.isActive());

        Warehouse updated = warehouseRepository.save(existingWarehouse);
        return toDTO(updated);
    }

    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));
        warehouseRepository.delete(warehouse);
    }

    public List<WarehouseDTO> getActiveWarehouses() {
        return warehouseRepository.findByActiveTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Méthodes de conversion
    private Warehouse toEntity(WarehouseDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.getId());
        warehouse.setName(dto.getName());
        warehouse.setVille(dto.getVille());
        warehouse.setActive(dto.isActive());
        return warehouse;
    }

    private WarehouseDTO toDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setVille(warehouse.getVille());
        dto.setActive(warehouse.isActive());
        return dto;
    }
}