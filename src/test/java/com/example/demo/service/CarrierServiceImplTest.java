package com.example.demo.service.impl;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.entity.Carrier;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.mapper.CarrierMapper;
import com.example.demo.repository.CarrierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierServiceImplTest {

    @Mock
    private CarrierRepository carrierRepository;

    @InjectMocks
    private CarrierServiceImpl carrierService;

    private Carrier carrier;
    private CarrierDTO carrierDTO;
    private UUID carrierId;

    @BeforeEach
    void setUp() {
        carrierId = UUID.randomUUID();

        carrier = new Carrier();
        carrier.setId(carrierId);
        carrier.setName("DHL Express");
        carrier.setContactEmail("contact@dhl.com");
        carrier.setContactPhone("+1234567890");
        carrier.setBaseShippingRate(BigDecimal.valueOf(15.50));
        carrier.setMaxDailyCapacity(100);
        carrier.setCurrentDailyShipments(25);
        carrier.setCutOffTime(LocalTime.of(17, 0));
        carrier.setStatus(CarrierStatus.ACTIVE);

        carrierDTO = new CarrierDTO();
        carrierDTO.setId(carrierId);
        carrierDTO.setName("DHL Express");
        carrierDTO.setContactEmail("contact@dhl.com");
        carrierDTO.setContactPhone("+1234567890");
        carrierDTO.setBaseShippingRate(BigDecimal.valueOf(15.50));
        carrierDTO.setMaxDailyCapacity(100);
        carrierDTO.setCurrentDailyShipments(25);
        carrierDTO.setCutOffTime(LocalTime.of(17, 0));
        carrierDTO.setStatus(CarrierStatus.ACTIVE);
    }

    @Test
    void createCarrier_ShouldReturnSavedCarrier() {
        when(carrierRepository.existsByName(carrierDTO.getName())).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        CarrierDTO result = carrierService.createCarrier(carrierDTO);

        assertNotNull(result);
        assertEquals(carrierDTO.getName(), result.getName());
        assertEquals(carrierDTO.getContactEmail(), result.getContactEmail());
        verify(carrierRepository, times(1)).existsByName(carrierDTO.getName());
        verify(carrierRepository, times(1)).save(any(Carrier.class));
    }

    @Test
    void createCarrier_WhenNameAlreadyExists_ShouldThrowException() {
        when(carrierRepository.existsByName(carrierDTO.getName())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> carrierService.createCarrier(carrierDTO));

        assertEquals("Un transporteur avec le nom '" + carrierDTO.getName() + "' existe déjà", exception.getMessage());
        verify(carrierRepository, times(1)).existsByName(carrierDTO.getName());
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void createCarrier_WhenCurrentDailyShipmentsIsNull_ShouldSetToZero() {
        carrierDTO.setCurrentDailyShipments(null);
        when(carrierRepository.existsByName(carrierDTO.getName())).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        carrierService.createCarrier(carrierDTO);

        verify(carrierRepository, times(1)).save(any(Carrier.class));
    }

    @Test
    void createCarrier_WhenStatusIsNull_ShouldSetToActive() {

        carrierDTO.setStatus(null);
        when(carrierRepository.existsByName(carrierDTO.getName())).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        carrierService.createCarrier(carrierDTO);

        verify(carrierRepository, times(1)).save(any(Carrier.class));
    }

    @Test
    void getCarrierById_ShouldReturnCarrier() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));

        CarrierDTO result = carrierService.getCarrierById(carrierId);

        assertNotNull(result);
        assertEquals(carrierId, result.getId());
        assertEquals(carrier.getName(), result.getName());
        verify(carrierRepository, times(1)).findById(carrierId);
    }

    @Test
    void getCarrierById_WhenNotFound_ShouldThrowException() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> carrierService.getCarrierById(carrierId));

        assertEquals("Transporteur non trouvé avec l'id: " + carrierId, exception.getMessage());
        verify(carrierRepository, times(1)).findById(carrierId);
    }

    @Test
    void getCarrierByName_ShouldReturnCarrier() {
        String carrierName = "DHL Express";
        when(carrierRepository.findByName(carrierName)).thenReturn(Optional.of(carrier));

        CarrierDTO result = carrierService.getCarrierByName(carrierName);

        assertNotNull(result);
        assertEquals(carrierName, result.getName());
        verify(carrierRepository, times(1)).findByName(carrierName);
    }

    @Test
    void getCarrierByName_WhenNotFound_ShouldThrowException() {
        String carrierName = "Non-existent Carrier";
        when(carrierRepository.findByName(carrierName)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> carrierService.getCarrierByName(carrierName));

        assertEquals("Transporteur non trouvé avec le nom: " + carrierName, exception.getMessage());
        verify(carrierRepository, times(1)).findByName(carrierName);
    }

    @Test
    void getAllCarriers_ShouldReturnAllCarriers() {
        List<Carrier> carriers = Arrays.asList(carrier, createAnotherCarrier());
        when(carrierRepository.findAll()).thenReturn(carriers);

        List<CarrierDTO> result = carrierService.getAllCarriers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carrierRepository, times(1)).findAll();
    }

    @Test
    void getCarriersByStatus_ShouldReturnStatusCarriers() {
        List<Carrier> carriers = Arrays.asList(carrier);
        when(carrierRepository.findByStatus(CarrierStatus.ACTIVE)).thenReturn(carriers);

        List<CarrierDTO> result = carrierService.getCarriersByStatus(CarrierStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carrierRepository, times(1)).findByStatus(CarrierStatus.ACTIVE);
    }

    @Test
    void getActiveCarriers_ShouldReturnActiveCarriers() {
        // Arrange
        List<Carrier> carriers = Arrays.asList(carrier);
        when(carrierRepository.findByStatus(CarrierStatus.ACTIVE)).thenReturn(carriers);

        List<CarrierDTO> result = carrierService.getActiveCarriers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carrierRepository, times(1)).findByStatus(CarrierStatus.ACTIVE);
    }

    @Test
    void deactivateCarrier_ShouldUpdateStatusToInactive() {

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        carrierService.deactivateCarrier(carrierId);

        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void activateCarrier_ShouldUpdateStatusToActive() {

        carrier.setStatus(CarrierStatus.INACTIVE);
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        carrierService.activateCarrier(carrierId);

        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void updateCarrier_ShouldUpdateAndReturnCarrier() {
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setName("DHL Updated");
        updateDTO.setContactEmail("updated@dhl.com");
        updateDTO.setContactPhone("+0987654321");
        updateDTO.setBaseShippingRate(BigDecimal.valueOf(20.0));
        updateDTO.setMaxDailyCapacity(150);
        updateDTO.setCurrentDailyShipments(30);
        updateDTO.setCutOffTime(LocalTime.of(18, 0));
        updateDTO.setStatus(CarrierStatus.INACTIVE);

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.existsByName(updateDTO.getName())).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        CarrierDTO result = carrierService.updateCarrier(carrierId, updateDTO);

        assertNotNull(result);
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).existsByName(updateDTO.getName());
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void updateCarrier_WhenNameExistsAndDifferent_ShouldThrowException() {
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setName("Existing Carrier");

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.existsByName(updateDTO.getName())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> carrierService.updateCarrier(carrierId, updateDTO));

        assertEquals("Un transporteur avec le nom '" + updateDTO.getName() + "' existe déjà", exception.getMessage());
        verify(carrierRepository, times(1)).existsByName(updateDTO.getName());
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void updateCarrier_WhenSameName_ShouldNotCheckExistence() {
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setName(carrier.getName());

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        CarrierDTO result = carrierService.updateCarrier(carrierId, updateDTO);

        assertNotNull(result);
        verify(carrierRepository, never()).existsByName(any());
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void updateCarrier_WhenCurrentDailyShipmentsIsNull_ShouldNotUpdateIt() {
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setCurrentDailyShipments(null);

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.existsByName(updateDTO.getName())).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        carrierService.updateCarrier(carrierId, updateDTO);

        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void updateCarrier_WhenStatusIsNull_ShouldNotUpdateIt() {
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setStatus(null);

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.existsByName(updateDTO.getName())).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        carrierService.updateCarrier(carrierId, updateDTO);

        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void deleteCarrier_ShouldDeleteCarrier() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        doNothing().when(carrierRepository).delete(carrier);

        carrierService.deleteCarrier(carrierId);

        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).delete(carrier);
    }

    @Test
    void updateCarrierStatus_ShouldUpdateStatus() {
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        CarrierDTO result = carrierService.updateCarrierStatus(carrierId, CarrierStatus.SUSPENDED);

        assertNotNull(result);
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void incrementDailyShipments_ShouldIncrementShipments() {
        carrier.setCurrentDailyShipments(50);
        carrier.setMaxDailyCapacity(100);

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        CarrierDTO result = carrierService.incrementDailyShipments(carrierId);

        assertNotNull(result);
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void incrementDailyShipments_WhenMaxCapacityReached_ShouldThrowException() {

        carrier.setCurrentDailyShipments(100);
        carrier.setMaxDailyCapacity(100);

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> carrierService.incrementDailyShipments(carrierId));

        assertEquals("Capacité quotidienne maximale atteinte pour le transporteur: " + carrier.getName(), exception.getMessage());
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void resetDailyShipments_ShouldResetToZero() {
        carrier.setCurrentDailyShipments(75);
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);

        CarrierDTO result = carrierService.resetDailyShipments(carrierId);

        assertNotNull(result);
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).save(carrier);
    }

    private Carrier createAnotherCarrier() {
        Carrier anotherCarrier = new Carrier();
        anotherCarrier.setId(UUID.randomUUID());
        anotherCarrier.setName("FedEx");
        anotherCarrier.setContactEmail("contact@fedex.com");
        anotherCarrier.setContactPhone("+1122334455");
        anotherCarrier.setBaseShippingRate(BigDecimal.valueOf(18.75));
        anotherCarrier.setMaxDailyCapacity(200);
        anotherCarrier.setCurrentDailyShipments(45);
        anotherCarrier.setCutOffTime(LocalTime.of(16, 30));
        anotherCarrier.setStatus(CarrierStatus.ACTIVE);
        return anotherCarrier;
    }
}