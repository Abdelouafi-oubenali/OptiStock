package com.example.demo.service;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.enums.CarrierStatus;

import java.util.List;
import java.util.UUID;

public interface CarrierService {
    CarrierDTO createCarrier(CarrierDTO carrierDTO);
    CarrierDTO getCarrierById(UUID id);
    CarrierDTO getCarrierByName(String name);
    List<CarrierDTO> getAllCarriers();
    List<CarrierDTO> getCarriersByStatus(CarrierStatus status);

    List<CarrierDTO> getActiveCarriers();
    void deactivateCarrier(UUID id);
    void activateCarrier(UUID id);

    CarrierDTO updateCarrier(UUID id, CarrierDTO carrierDTO);
    void deleteCarrier(UUID id);
    CarrierDTO updateCarrierStatus(UUID id, CarrierStatus status);
    CarrierDTO incrementDailyShipments(UUID id);
    CarrierDTO resetDailyShipments(UUID id);
}