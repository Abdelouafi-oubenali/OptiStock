package com.example.demo.repository;

import com.example.demo.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
    List<SalesOrderLine> findBySalesOrderId(UUID salesOrderId);
    List<SalesOrderLine> findByProductId(UUID productId);
}