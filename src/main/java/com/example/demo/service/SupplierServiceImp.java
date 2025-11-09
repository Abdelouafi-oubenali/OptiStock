package com.example.demo.service;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImp implements SupplierService{

    private final SupplierRepository supplierRepository;

    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierDTO.getName());
        supplier.setContactInfo(supplierDTO.getContactInfo());
        supplier.setActive(supplierDTO.getActive() != null ? supplierDTO.getActive() : true);

        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToDTO(savedSupplier);
    }

    public SupplierDTO getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return convertToDTO(supplier);
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO updateSupplier(UUID id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplier.setName(supplierDTO.getName());
        supplier.setContactInfo(supplierDTO.getContactInfo());
        if (supplierDTO.getActive() != null) {
            supplier.setActive(supplierDTO.getActive());
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToDTO(updatedSupplier);
    }

    public void deleteSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        supplierRepository.delete(supplier);
    }

    public List<SupplierDTO> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> getInactiveSuppliers() {
        return supplierRepository.findByActiveFalse()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean supplierExists(String name) {
        return supplierRepository.existsByName(name);
    }

    public SupplierDTO getSupplierByName(String name) {
        Supplier supplier = supplierRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Supplier not found with name: " + name));
        return convertToDTO(supplier);
    }

    private SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setContactInfo(supplier.getContactInfo());
        dto.setActive(supplier.getActive());
        return dto;
    }

    private Supplier convertToEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setName(dto.getName());
        supplier.setContactInfo(dto.getContactInfo());
        supplier.setActive(dto.getActive());
        return supplier;
    }
}