package com.example.demo.repository;

import com.example.demo.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Optional<Supplier> findByName(String name);

    boolean existsByName(String name);

    List<Supplier> findByActiveTrue();

    List<Supplier> findByActiveFalse();

    List<Supplier> findByNameContainingIgnoreCase(String name);
}