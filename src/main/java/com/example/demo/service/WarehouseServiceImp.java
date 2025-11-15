package com.example.demo.service;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.entity.Warehouse;
import com.example.demo.mapper.WarehouseMapper;
import com.example.demo.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImp implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper mapper = WarehouseMapper.INSTANCE;

    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        if (warehouseRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Un entrepôt avec ce nom existe déjà");
        }
        Warehouse warehouse = mapper.toEntity(dto);
        Warehouse saved = warehouseRepository.save(warehouse);
        return mapper.toDTO(saved);
    }

    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public WarehouseDTO getWarehouseById(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));
        return mapper.toDTO(warehouse);
    }

    public WarehouseDTO updateWarehouse(UUID id, WarehouseDTO dto) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));

        if (!existing.getName().equals(dto.getName()) && warehouseRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Un entrepôt avec ce nom existe déjà");
        }

        existing.setName(dto.getName());
        existing.setVille(dto.getVille());
        existing.setActive(dto.isActive());

        Warehouse updated = warehouseRepository.save(existing);
        return mapper.toDTO(updated);
    }

    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));
        warehouseRepository.delete(warehouse);
    }

    public List<WarehouseDTO> getActiveWarehouses() {
        return warehouseRepository.findByActiveTrue()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
}
