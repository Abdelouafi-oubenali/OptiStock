package com.example.demo.service;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.entity.Warehouse;
import com.example.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WarehouseServiceImpTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseServiceImp warehouseService;

    private Warehouse warehouse;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        warehouse = new Warehouse();
        warehouse.setId(UUID.randomUUID());
        warehouse.setName("Central");
        warehouse.setVille("Paris");
        warehouse.setActive(true);

        warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(warehouse.getId());
        warehouseDTO.setName("Central");
        warehouseDTO.setVille("Paris");
        warehouseDTO.setActive(true);
    }

    @Test
    void testCreateWarehouse_Success() {
        when(warehouseRepository.existsByName("Central")).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDTO result = warehouseService.createWarehouse(warehouseDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Central");

        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    void testCreateWarehouse_AlreadyExists() {
        when(warehouseRepository.existsByName("Central")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                warehouseService.createWarehouse(warehouseDTO)
        );

        assertThat(exception.getMessage()).isEqualTo("Un entrepôt avec ce nom existe déjà");
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void testGetAllWarehouses() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseDTO> list = warehouseService.getAllWarehouses();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("Central");
    }

    @Test
    void testGetWarehouseById_Success() {
        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));

        WarehouseDTO result = warehouseService.getWarehouseById(warehouse.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Central");
    }

    @Test
    void testGetWarehouseById_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(warehouseRepository.findById(randomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                warehouseService.getWarehouseById(randomId)
        );

        assertThat(exception.getMessage()).contains("Entrepôt non trouvé");
    }

    @Test
    void testDeleteWarehouse() {
        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));

        warehouseService.deleteWarehouse(warehouse.getId());

        verify(warehouseRepository, times(1)).delete(warehouse);
    }
}
