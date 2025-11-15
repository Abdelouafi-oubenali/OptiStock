package com.example.demo.service.impl;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.entity.Carrier;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.mapper.CarrierMapper;
import com.example.demo.repository.CarrierRepository;
import com.example.demo.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;

    private final CarrierMapper carrierMapper = CarrierMapper.INSTANCE;

    @Override
    @Transactional
    public CarrierDTO createCarrier(CarrierDTO carrierDTO) {
        if (carrierRepository.existsByName(carrierDTO.getName())) {
            throw new RuntimeException("Un transporteur avec le nom '" + carrierDTO.getName() + "' existe déjà");
        }

        Carrier carrier = carrierMapper.toEntity(carrierDTO);
        if (carrier.getCurrentDailyShipments() == null) {
            carrier.setCurrentDailyShipments(0);
        }
        if (carrier.getStatus() == null) {
            carrier.setStatus(CarrierStatus.ACTIVE);
        }

        Carrier savedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDTO(savedCarrier);
    }

    @Override
    public CarrierDTO getCarrierById(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        return carrierMapper.toDTO(carrier);
    }

    @Override
    public CarrierDTO getCarrierByName(String name) {
        Carrier carrier = carrierRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec le nom: " + name));
        return carrierMapper.toDTO(carrier);
    }

    @Override
    public List<CarrierDTO> getAllCarriers() {
        return carrierRepository.findAll()
                .stream()
                .map(carrierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarrierDTO> getCarriersByStatus(CarrierStatus status) {
        return carrierRepository.findByStatus(status)
                .stream()
                .map(carrierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarrierDTO> getActiveCarriers() {
        return getCarriersByStatus(CarrierStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void deactivateCarrier(UUID id) {
        updateCarrierStatus(id, CarrierStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void activateCarrier(UUID id) {
        updateCarrierStatus(id, CarrierStatus.ACTIVE);
    }

    @Override
    @Transactional
    public CarrierDTO updateCarrier(UUID id, CarrierDTO carrierDTO) {
        Carrier existingCarrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));

        if (!existingCarrier.getName().equals(carrierDTO.getName()) &&
                carrierRepository.existsByName(carrierDTO.getName())) {
            throw new RuntimeException("Un transporteur avec le nom '" + carrierDTO.getName() + "' existe déjà");
        }

        existingCarrier.setName(carrierDTO.getName());
        existingCarrier.setContactEmail(carrierDTO.getContactEmail());
        existingCarrier.setContactPhone(carrierDTO.getContactPhone());
        existingCarrier.setBaseShippingRate(carrierDTO.getBaseShippingRate());
        existingCarrier.setMaxDailyCapacity(carrierDTO.getMaxDailyCapacity());

        if (carrierDTO.getCurrentDailyShipments() != null) {
            existingCarrier.setCurrentDailyShipments(carrierDTO.getCurrentDailyShipments());
        }

        existingCarrier.setCutOffTime(carrierDTO.getCutOffTime());

        if (carrierDTO.getStatus() != null) {
            existingCarrier.setStatus(carrierDTO.getStatus());
        }

        Carrier updatedCarrier = carrierRepository.save(existingCarrier);
        return carrierMapper.toDTO(updatedCarrier);
    }

    @Override
    @Transactional
    public void deleteCarrier(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        carrierRepository.delete(carrier);
    }

    @Override
    @Transactional
    public CarrierDTO updateCarrierStatus(UUID id, CarrierStatus status) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        carrier.setStatus(status);
        Carrier updatedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDTO(updatedCarrier);
    }

    @Override
    @Transactional
    public CarrierDTO incrementDailyShipments(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));

        if (carrier.getCurrentDailyShipments() >= carrier.getMaxDailyCapacity()) {
            throw new RuntimeException("Capacité quotidienne maximale atteinte pour le transporteur: " + carrier.getName());
        }

        carrier.setCurrentDailyShipments(carrier.getCurrentDailyShipments() + 1);
        Carrier updatedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDTO(updatedCarrier);
    }

    @Override
    @Transactional
    public CarrierDTO resetDailyShipments(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        carrier.setCurrentDailyShipments(0);
        Carrier updatedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDTO(updatedCarrier);
    }
}
