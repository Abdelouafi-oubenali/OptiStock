package com.example.demo.service.impl;

import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.entity.SalesOrder;
import com.example.demo.entity.SalesOrderLine;
import com.example.demo.mapper.SalesOrderLineMapper;
import com.example.demo.repository.*;
import com.example.demo.service.SalesOrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entity.Supplier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderLineServiceImpl implements SalesOrderLineService {

    private final SalesOrderLineRepository salesOrderLineRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private final SalesOrderLineMapper lineMapper = SalesOrderLineMapper.INSTANCE;

    @Override
    @Transactional
    public SalesOrderLineDTO createSalesOrderLine(SalesOrderLineDTO salesOrderLineDTO) {
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderLineDTO.getSales_order_id())
                .orElseThrow(() -> new RuntimeException("Commande de vente introuvable avec l'id: " + salesOrderLineDTO.getSales_order_id()));

        Product product = productRepository.findById(salesOrderLineDTO.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'id: " + salesOrderLineDTO.getProduct_id()));

        List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
        if (inventories == null || inventories.isEmpty()) {
            throw new RuntimeException("Aucun inventaire trouvé pour le produit id: " + product.getId());
        }

        int totalQty = inventories.stream().mapToInt(Inventory::getQtyOnHand).sum();
        int orderedQty = salesOrderLineDTO.getQuantity();
        int backorderQty = 0;

        if (orderedQty > totalQty) {
            backorderQty = orderedQty - totalQty;
            orderedQty = totalQty;
        }

        SalesOrderLine salesOrderLine = lineMapper.toEntity(salesOrderLineDTO);
        salesOrderLine.setSalesOrder(salesOrder);
        salesOrderLine.setProduct(product);
        salesOrderLine.setBackorder(backorderQty);

        SalesOrderLine savedLine = salesOrderLineRepository.save(salesOrderLine);

        int remainingToDeduct = orderedQty;
        for (Inventory inv : inventories) {
            if (remainingToDeduct <= 0) break;
            int deduct = Math.min(inv.getQtyOnHand(), remainingToDeduct);
            inv.setQtyOnHand(inv.getQtyOnHand() - deduct);
            inventoryRepository.save(inv);
            remainingToDeduct -= deduct;
        }

        if (backorderQty > 0) {
            createAutomaticPurchaseOrder(product, backorderQty);
        }

        return lineMapper.toDTO(savedLine);
    }

    private void createAutomaticPurchaseOrder(Product product, int backorderQty) {
        try {
            Supplier supplier = supplierRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Aucun fournisseur trouvé"));
            var createdBy = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé"));

            var purchaseOrder = new com.example.demo.entity.PurchaseOrder();
            purchaseOrder.setSupplier(supplier);
            purchaseOrder.setCreatedBy(createdBy);
            purchaseOrder.setStatus(com.example.demo.enums.PurchaseOrderStatus.CREATED);
            purchaseOrder.setExpectedDelivery(LocalDateTime.now().plusDays(7));

            var savedOrder = purchaseOrderRepository.save(purchaseOrder);

            var orderLine = new com.example.demo.entity.PurchaseOrderLine();
            orderLine.setPurchaseOrder(savedOrder);
            orderLine.setProduct(product);
            orderLine.setQuantity(backorderQty);
            orderLine.setBackorder(backorderQty);
            orderLine.setUnitPrice(new BigDecimal("100.00"));

            purchaseOrderLineRepository.save(orderLine);

        } catch (Exception e) {
            System.err.println("Erreur lors de la création automatique du PurchaseOrder: " + e.getMessage());
        }
    }

    @Override
    public SalesOrderLineDTO getSalesOrderLineById(UUID id) {
        return lineMapper.toDTO(salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrderLine not found with id: " + id)));
    }

    @Override
    public List<SalesOrderLineDTO> getAllSalesOrderLines() {
        return salesOrderLineRepository.findAll().stream()
                .map(lineMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOrderLineDTO> getSalesOrderLinesByOrder(UUID salesOrderId) {
        return salesOrderLineRepository.findBySalesOrderId(salesOrderId).stream()
                .map(lineMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOrderLineDTO> getSalesOrderLinesByProduct(UUID productId) {
        return salesOrderLineRepository.findByProductId(productId).stream()
                .map(lineMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesOrderLineDTO updateSalesOrderLine(UUID id, SalesOrderLineDTO dto) {
        SalesOrderLine existingLine = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrderLine not found with id: " + id));

        existingLine.setQuantity(dto.getQuantity());
        existingLine.setUnitPrice(dto.getUnitPrice());
        existingLine.setBackorder(dto.getBackorder());

        if (dto.getSales_order_id() != null) {
            existingLine.setSalesOrder(salesOrderRepository.findById(dto.getSales_order_id())
                    .orElseThrow(() -> new RuntimeException("SalesOrder not found with id: " + dto.getSales_order_id())));
        }

        if (dto.getProduct_id() != null) {
            existingLine.setProduct(productRepository.findById(dto.getProduct_id())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProduct_id())));
        }

        return lineMapper.toDTO(salesOrderLineRepository.save(existingLine));
    }

    @Override
    @Transactional
    public void deleteSalesOrderLine(UUID id) {
        salesOrderLineRepository.delete(salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesOrderLine not found with id: " + id)));
    }

    @Override
    @Transactional
    public void deleteSalesOrderLinesByOrder(UUID salesOrderId) {
        salesOrderLineRepository.deleteAll(salesOrderLineRepository.findBySalesOrderId(salesOrderId));
    }
}
