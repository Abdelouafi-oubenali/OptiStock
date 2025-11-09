package com.example.demo.service.impl;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.entity.Carrier;
import com.example.demo.enums.CarrierStatus;
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

    @Override
    @Transactional
    public CarrierDTO createCarrier(CarrierDTO carrierDTO) {
        // Vérifier si le nom existe déjà
        if (carrierRepository.existsByName(carrierDTO.getName())) {
            throw new RuntimeException("Un transporteur avec le nom '" + carrierDTO.getName() + "' existe déjà");
        }

        Carrier carrier = new Carrier();
        carrier.setName(carrierDTO.getName());
        carrier.setContactEmail(carrierDTO.getContactEmail());
        carrier.setContactPhone(carrierDTO.getContactPhone());
        carrier.setBaseShippingRate(carrierDTO.getBaseShippingRate());
        carrier.setMaxDailyCapacity(carrierDTO.getMaxDailyCapacity());
        carrier.setCurrentDailyShipments(carrierDTO.getCurrentDailyShipments() != null ?
                carrierDTO.getCurrentDailyShipments() : 0);
        carrier.setCutOffTime(carrierDTO.getCutOffTime());
        carrier.setStatus(carrierDTO.getStatus() != null ?
                carrierDTO.getStatus() : CarrierStatus.ACTIVE);

        Carrier savedCarrier = carrierRepository.save(carrier);
        return convertToDTO(savedCarrier);
    }

    @Override
    public CarrierDTO getCarrierById(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        return convertToDTO(carrier);
    }

    @Override
    public CarrierDTO getCarrierByName(String name) {
        Carrier carrier = carrierRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec le nom: " + name));
        return convertToDTO(carrier);
    }

    @Override
    public List<CarrierDTO> getAllCarriers() {
        return carrierRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarrierDTO> getCarriersByStatus(CarrierStatus status) {
        return carrierRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarrierDTO> getActiveCarriers() {
        return carrierRepository.findByStatus(CarrierStatus.ACTIVE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deactivateCarrier(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        carrier.setStatus(CarrierStatus.INACTIVE);
        carrierRepository.save(carrier);
    }

    @Override
    @Transactional
    public void activateCarrier(UUID id) {
        // AJOUTER CETTE IMPLÉMENTATION
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        carrier.setStatus(CarrierStatus.ACTIVE);
        carrierRepository.save(carrier);
    }

    @Override
    @Transactional
    public CarrierDTO updateCarrier(UUID id, CarrierDTO carrierDTO) {
        Carrier existingCarrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));

        // Vérifier l'unicité du nom (sauf pour l'entité actuelle)
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
        return convertToDTO(updatedCarrier);
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
        return convertToDTO(updatedCarrier);
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
        return convertToDTO(updatedCarrier);
    }

    @Override
    @Transactional
    public CarrierDTO resetDailyShipments(UUID id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur non trouvé avec l'id: " + id));
        carrier.setCurrentDailyShipments(0);
        Carrier updatedCarrier = carrierRepository.save(carrier);
        return convertToDTO(updatedCarrier);
    }

    private CarrierDTO convertToDTO(Carrier carrier) {
        return CarrierDTO.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .contactEmail(carrier.getContactEmail())
                .contactPhone(carrier.getContactPhone())
                .baseShippingRate(carrier.getBaseShippingRate())
                .maxDailyCapacity(carrier.getMaxDailyCapacity())
                .currentDailyShipments(carrier.getCurrentDailyShipments())
                .cutOffTime(carrier.getCutOffTime())
                .status(carrier.getStatus())
                .build();
    }
}