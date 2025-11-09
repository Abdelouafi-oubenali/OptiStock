package com.example.demo.service;

import com.example.demo.dto.SupplierDTO;

import java.util.List;
import java.util.UUID;

public interface SupplierService {

    SupplierDTO createSupplier(SupplierDTO supplierDTO);

    SupplierDTO getSupplierById(UUID id);

    List<SupplierDTO> getAllSuppliers();

    SupplierDTO updateSupplier(UUID id, SupplierDTO supplierDTO);

    void deleteSupplier(UUID id);

    List<SupplierDTO> getActiveSuppliers();

    List<SupplierDTO> getInactiveSuppliers();

    List<SupplierDTO> searchSuppliersByName(String name);

    boolean supplierExists(String name);

    SupplierDTO getSupplierByName(String name);
}