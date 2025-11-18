package com.example.demo.service;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.entity.Supplier;
import com.example.demo.mapper.SupplierMapper;
import com.example.demo.repository.SupplierRepository;
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
class SupplierServiceImpTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImp supplierService;

    private Supplier supplier;
    private SupplierDTO supplierDTO;
    private UUID supplierId;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();

        supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");
        supplier.setContactInfo("test@supplier.com");
        supplier.setActive(true);

        supplierDTO = new SupplierDTO();
        supplierDTO.setId(supplierId);
        supplierDTO.setName("Test Supplier");
        supplierDTO.setContactInfo("test@supplier.com");
        supplierDTO.setActive(true);
    }

    @Test
    void createSupplier_ShouldReturnSavedSupplier() {
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        SupplierDTO result = supplierService.createSupplier(supplierDTO);

        assertNotNull(result);
        assertEquals(supplierDTO.getName(), result.getName());
        assertEquals(supplierDTO.getContactInfo(), result.getContactInfo());
        assertTrue(result.getActive());
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void createSupplier_WhenActiveIsNull_ShouldSetActiveToTrue() {
        supplierDTO.setActive(null);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        SupplierDTO result = supplierService.createSupplier(supplierDTO);

        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    void getSupplierById_ShouldReturnSupplier() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));

        SupplierDTO result = supplierService.getSupplierById(supplierId);

        assertNotNull(result);
        assertEquals(supplierId, result.getId());
        assertEquals(supplier.getName(), result.getName());
        verify(supplierRepository, times(1)).findById(supplierId);
    }

    @Test
    void getSupplierById_WhenNotFound_ShouldThrowException() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> supplierService.getSupplierById(supplierId));

        assertEquals("Supplier not found with id: " + supplierId, exception.getMessage());
        verify(supplierRepository, times(1)).findById(supplierId);
    }

    @Test
    void getAllSuppliers_ShouldReturnAllSuppliers() {
        List<Supplier> suppliers = Arrays.asList(supplier, createAnotherSupplier());
        when(supplierRepository.findAll()).thenReturn(suppliers);

        List<SupplierDTO> result = supplierService.getAllSuppliers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    void updateSupplier_ShouldUpdateAndReturnSupplier() {
        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("Updated Supplier");
        updateDTO.setContactInfo("updated@supplier.com");
        updateDTO.setActive(false);

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        SupplierDTO result = supplierService.updateSupplier(supplierId, updateDTO);

        assertNotNull(result);
        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void updateSupplier_WhenNotFound_ShouldThrowException() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> supplierService.updateSupplier(supplierId, supplierDTO));

        assertEquals("Supplier not found with id: " + supplierId, exception.getMessage());
        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierRepository, never()).save(any(Supplier.class));
    }

    @Test
    void deleteSupplier_ShouldDeleteSupplier() {

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierRepository).delete(supplier);

        supplierService.deleteSupplier(supplierId);

        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierRepository, times(1)).delete(supplier);
    }

    @Test
    void deleteSupplier_WhenNotFound_ShouldThrowException() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> supplierService.deleteSupplier(supplierId));

        assertEquals("Supplier not found with id: " + supplierId, exception.getMessage());
        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierRepository, never()).delete(any(Supplier.class));
    }

    @Test
    void getActiveSuppliers_ShouldReturnOnlyActiveSuppliers() {
        List<Supplier> activeSuppliers = Arrays.asList(supplier);
        when(supplierRepository.findByActiveTrue()).thenReturn(activeSuppliers);

        List<SupplierDTO> result = supplierService.getActiveSuppliers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplierRepository, times(1)).findByActiveTrue();
    }

    @Test
    void getInactiveSuppliers_ShouldReturnOnlyInactiveSuppliers() {
        Supplier inactiveSupplier = createInactiveSupplier();
        List<Supplier> inactiveSuppliers = Arrays.asList(inactiveSupplier);
        when(supplierRepository.findByActiveFalse()).thenReturn(inactiveSuppliers);

        List<SupplierDTO> result = supplierService.getInactiveSuppliers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplierRepository, times(1)).findByActiveFalse();
    }

    @Test
    void searchSuppliersByName_ShouldReturnMatchingSuppliers() {
        String searchName = "test";
        List<Supplier> matchingSuppliers = Arrays.asList(supplier);
        when(supplierRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(matchingSuppliers);

        List<SupplierDTO> result = supplierService.searchSuppliersByName(searchName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplierRepository, times(1)).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    void supplierExists_ShouldReturnTrueWhenSupplierExists() {
        // Arrange
        String supplierName = "Existing Supplier";
        when(supplierRepository.existsByName(supplierName)).thenReturn(true);

        boolean result = supplierService.supplierExists(supplierName);

        assertTrue(result);
        verify(supplierRepository, times(1)).existsByName(supplierName);
    }

    @Test
    void supplierExists_ShouldReturnFalseWhenSupplierDoesNotExist() {
        String supplierName = "Non-existing Supplier";
        when(supplierRepository.existsByName(supplierName)).thenReturn(false);

        boolean result = supplierService.supplierExists(supplierName);

        assertFalse(result);
        verify(supplierRepository, times(1)).existsByName(supplierName);
    }

    @Test
    void getSupplierByName_ShouldReturnSupplier() {
        String supplierName = "Test Supplier";
        when(supplierRepository.findByName(supplierName)).thenReturn(Optional.of(supplier));

        SupplierDTO result = supplierService.getSupplierByName(supplierName);

        assertNotNull(result);
        assertEquals(supplierName, result.getName());
        verify(supplierRepository, times(1)).findByName(supplierName);
    }

    @Test
    void getSupplierByName_WhenNotFound_ShouldThrowException() {
        // Arrange
        String supplierName = "Non-existing Supplier";
        when(supplierRepository.findByName(supplierName)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> supplierService.getSupplierByName(supplierName));

        assertEquals("Supplier not found with name: " + supplierName, exception.getMessage());
        verify(supplierRepository, times(1)).findByName(supplierName);
    }

    private Supplier createAnotherSupplier() {
        Supplier anotherSupplier = new Supplier();
        anotherSupplier.setId(UUID.randomUUID());
        anotherSupplier.setName("Another Supplier");
        anotherSupplier.setContactInfo("another@supplier.com");
        anotherSupplier.setActive(true);
        return anotherSupplier;
    }

    private Supplier createInactiveSupplier() {
        Supplier inactiveSupplier = new Supplier();
        inactiveSupplier.setId(UUID.randomUUID());
        inactiveSupplier.setName("Inactive Supplier");
        inactiveSupplier.setContactInfo("inactive@supplier.com");
        inactiveSupplier.setActive(false);
        return inactiveSupplier;
    }
}