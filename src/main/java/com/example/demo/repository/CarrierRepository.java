package com.example.demo.repository;

import com.example.demo.entity.Carrier;
import com.example.demo.enums.CarrierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, UUID> {

    Optional<Carrier> findByName(String name);

    boolean existsByName(String name);

    List<Carrier> findByStatus(CarrierStatus status);

    List<Carrier> findByNameContainingIgnoreCase(String name);

    List<Carrier> findByStatusAndCurrentDailyShipmentsLessThan(CarrierStatus status, Integer maxDailyCapacity);
}