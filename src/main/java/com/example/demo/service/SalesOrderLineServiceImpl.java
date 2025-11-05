package com.example.demo.service.impl;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.SalesOrderLine;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.SalesOrderLineRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.SalesOrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderLineServiceImpl implements SalesOrderLineService {

    private final SalesOrderLineRepository salesOrderLineRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository ;

    @Override
    @Transactional

    public SalesOrderLineDTO createSalesOrderLine(SalesOrderLineDTO salesOrderLineDTO) {
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderLineDTO.getSales_order_id())
                .orElseThrow(() -> new RuntimeException("Commande de vente introuvable avec l'id: " + salesOrderLineDTO.getSales_order_id()));

        Product product = productRepository.findById(salesOrderLineDTO.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'id: " + salesOrderLineDTO.getProduct_id()));

        List<Inventory> inventories = inventoryRepository.findByProductId(salesOrderLineDTO.getProduct_id());

        if (inventories == null || inventories.isEmpty()) {
            throw new RuntimeException("Aucun inventaire trouvé pour le produit id: " + salesOrderLineDTO.getProduct_id());
        }

        int totalQty = inventories.stream().mapToInt(Inventory::getQtyOnHand).sum();

        if (salesOrderLineDTO.getQuantity() > totalQty) {
            throw new RuntimeException("Stock insuffisant pour le produit id: " + salesOrderLineDTO.getProduct_id());
        }

        SalesOrderLine salesOrderLine = new SalesOrderLine();
        salesOrderLine.setSalesOrder(salesOrder);
        salesOrderLine.setProduct(product);
        salesOrderLine.setQuantity(salesOrderLineDTO.getQuantity());
        salesOrderLine.setUnitPrice(salesOrderLineDTO.getUnitPrice());
        salesOrderLine.setBackorder(salesOrderLineDTO.isBackorder());

        SalesOrderLine savedLine = salesOrderLineRepository.save(salesOrderLine);

        int remainingToDeduct = salesOrderLineDTO.getQuantity();

        for (Inventory inv : inventories) {
            if (remainingToDeduct <= 0) break;
            int available = inv.getQtyOnHand();
            int deduct = Math.min(available, remainingToDeduct);
            inv.setQtyOnHand(available - deduct);
            inventoryRepository.save(inv);
            remainingToDeduct -= deduct;
        }

        return convertToDTO(savedLine);
    }

    @Override
    public SalesOrderLineDTO getSalesOrderLineById(UUID id) {
        SalesOrderLine salesOrderLine = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrderLine not found with id: " + id));
        return convertToDTO(salesOrderLine);
    }

    @Override
    public List<SalesOrderLineDTO> getAllSalesOrderLines() {
        return salesOrderLineRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOrderLineDTO> getSalesOrderLinesByOrder(UUID salesOrderId) {
        return salesOrderLineRepository.findBySalesOrderId(salesOrderId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOrderLineDTO> getSalesOrderLinesByProduct(UUID productId) {
        return salesOrderLineRepository.findByProductId(productId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesOrderLineDTO updateSalesOrderLine(UUID id, SalesOrderLineDTO salesOrderLineDTO) {
        SalesOrderLine existingLine = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrderLine not found with id: " + id));

        // Mettre à jour les champs de base
        existingLine.setQuantity(salesOrderLineDTO.getQuantity());
        existingLine.setUnitPrice(salesOrderLineDTO.getUnitPrice());
        existingLine.setBackorder(salesOrderLineDTO.isBackorder());

        // Mettre à jour les relations si fournies
        if (salesOrderLineDTO.getSales_order_id() != null) {
            SalesOrder salesOrder = salesOrderRepository.findById(salesOrderLineDTO.getSales_order_id())
                    .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + salesOrderLineDTO.getSales_order_id()));
            existingLine.setSalesOrder(salesOrder);
        }

        if (salesOrderLineDTO.getProduct_id() != null) {
            Product product = productRepository.findById(salesOrderLineDTO.getProduct_id())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + salesOrderLineDTO.getProduct_id()));
            existingLine.setProduct(product);
        }

        SalesOrderLine updatedLine = salesOrderLineRepository.save(existingLine);
        return convertToDTO(updatedLine);
    }

    @Override
    @Transactional
    public void deleteSalesOrderLine(UUID id) {
        SalesOrderLine salesOrderLine = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrderLine not found with id: " + id));
        salesOrderLineRepository.delete(salesOrderLine);
    }

    @Override
    @Transactional
    public void deleteSalesOrderLinesByOrder(UUID salesOrderId) {
        List<SalesOrderLine> orderLines = salesOrderLineRepository.findBySalesOrderId(salesOrderId);
        salesOrderLineRepository.deleteAll(orderLines);
    }

    private SalesOrderLineDTO convertToDTO(SalesOrderLine salesOrderLine) {
        SalesOrderLineDTO dto = new SalesOrderLineDTO();
        dto.setId(salesOrderLine.getId());
        dto.setSales_order_id(salesOrderLine.getSalesOrder().getId());
        dto.setProduct_id(salesOrderLine.getProduct().getId());
        dto.setQuantity(salesOrderLine.getQuantity());
        dto.setUnitPrice(salesOrderLine.getUnitPrice());
        dto.setBackorder(salesOrderLine.isBackorder());
        return dto;
    }
}