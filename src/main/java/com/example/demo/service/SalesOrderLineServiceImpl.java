package com.example.demo.service.impl;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderLineDTO;
import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.repository.*;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.SalesOrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderService purchaseOrderService;

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
        int orderedQty = salesOrderLineDTO.getQuantity();
        int backorderQty = 0;

        if (orderedQty > totalQty) {
            backorderQty = orderedQty - totalQty;
            orderedQty = totalQty;
        }

        SalesOrderLine salesOrderLine = new SalesOrderLine();
        salesOrderLine.setSalesOrder(salesOrder);
        salesOrderLine.setProduct(product);
        salesOrderLine.setQuantity(salesOrderLineDTO.getQuantity());
        salesOrderLine.setUnitPrice(salesOrderLineDTO.getUnitPrice());
        salesOrderLine.setBackorder(backorderQty);

        SalesOrderLine savedLine = salesOrderLineRepository.save(salesOrderLine);

        int remainingToDeduct = orderedQty;
        for (Inventory inv : inventories) {
            if (remainingToDeduct <= 0) break;
            int available = inv.getQtyOnHand();
            int deduct = Math.min(available, remainingToDeduct);
            inv.setQtyOnHand(available - deduct);
            inventoryRepository.save(inv);
            remainingToDeduct -= deduct;
        }

        if (backorderQty > 0) {
            createAutomaticPurchaseOrder(product, backorderQty);
        }

        return convertToDTO(savedLine);
    }

    private void createAutomaticPurchaseOrder(Product product, int backorderQty) {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            if (suppliers.isEmpty()) {
                throw new RuntimeException("Aucun fournisseur trouvé dans la base de données");
            }
            Supplier supplier = suppliers.get(0);

            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new RuntimeException("Aucun utilisateur trouvé dans la base de données");
            }
            User createdBy = users.get(0);

            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setSupplier(supplier);
            purchaseOrder.setCreatedBy(createdBy);
            purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);
            purchaseOrder.setExpectedDelivery(LocalDateTime.now().plusDays(7));

            PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

            PurchaseOrderLine orderLine = new PurchaseOrderLine();
            orderLine.setPurchaseOrder(savedOrder);
            orderLine.setProduct(product);
            orderLine.setQuantity(backorderQty);
            orderLine.setBackorder(backorderQty) ;

            BigDecimal unitPrice = determinePurchasePrice(product);
            orderLine.setUnitPrice(unitPrice);

            purchaseOrderLineRepository.save(orderLine);

        } catch (Exception e) {
            System.err.println("Erreur lors de la création automatique du PurchaseOrder: " + e.getMessage());
        }
    }

    private BigDecimal determinePurchasePrice(Product product) {

        return new BigDecimal("100.00");
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

        existingLine.setQuantity(salesOrderLineDTO.getQuantity());
        existingLine.setUnitPrice(salesOrderLineDTO.getUnitPrice());
        existingLine.setBackorder(salesOrderLineDTO.getBackorder());

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
        dto.setBackorder(salesOrderLine.getBackorder());
        return dto; //mabstract .
    }
}