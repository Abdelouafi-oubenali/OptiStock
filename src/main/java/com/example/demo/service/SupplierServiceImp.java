package com.example.demo.service;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.entity.Supplier;
import com.example.demo.mapper.SupplierMapper;
import com.example.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImp implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper mapper = SupplierMapper.INSTANCE;

    public SupplierDTO createSupplier(SupplierDTO dto) {
        Supplier supplier = mapper.toEntity(dto);
        if (supplier.getActive() == null) supplier.setActive(true);
        Supplier saved = supplierRepository.save(supplier);
        return mapper.toDTO(saved);
    }

    public SupplierDTO getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return mapper.toDTO(supplier);
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO updateSupplier(UUID id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplier.setName(dto.getName());
        supplier.setContactInfo(dto.getContactInfo());
        if (dto.getActive() != null) supplier.setActive(dto.getActive());

        return mapper.toDTO(supplierRepository.save(supplier));
    }

    public void deleteSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        supplierRepository.delete(supplier);
    }

    public List<SupplierDTO> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> getInactiveSuppliers() {
        return supplierRepository.findByActiveFalse()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean supplierExists(String name) {
        return supplierRepository.existsByName(name);
    }

    public SupplierDTO getSupplierByName(String name) {
        Supplier supplier = supplierRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Supplier not found with name: " + name));
        return mapper.toDTO(supplier);
    }
}
