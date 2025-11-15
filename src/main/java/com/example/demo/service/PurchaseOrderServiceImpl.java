package com.example.demo.service.impl;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderLineDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.mapper.PurchaseOrderLineMapper;
import com.example.demo.mapper.PurchaseOrderMapper;
import com.example.demo.repository.*;
import com.example.demo.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    private final PurchaseOrderMapper orderMapper = PurchaseOrderMapper.INSTANCE;
    private final PurchaseOrderLineMapper lineMapper = PurchaseOrderLineMapper.INSTANCE;

    @Override
    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id: " + purchaseOrderDTO.getSupplierId()));

        User createdBy = userRepository.findById(purchaseOrderDTO.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + purchaseOrderDTO.getCreatedByUserId()));

        PurchaseOrder purchaseOrder = orderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setCreatedBy(createdBy);
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        if (purchaseOrderDTO.getOrderLines() != null && !purchaseOrderDTO.getOrderLines().isEmpty()) {
            List<PurchaseOrderLine> orderLines = purchaseOrderDTO.getOrderLines().stream()
                    .map(lineDTO -> {
                        PurchaseOrderLine line = lineMapper.toEntity(lineDTO);
                        line.setPurchaseOrder(savedOrder);
                        Product product = productRepository.findById(lineDTO.getProductId())
                                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + lineDTO.getProductId()));
                        line.setProduct(product);
                        return line;
                    })
                    .collect(Collectors.toList());
            purchaseOrderLineRepository.saveAll(orderLines);
        }

        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));
        return orderMapper.toDTO(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll()
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderDTO updatePurchaseOrder(UUID id, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        if (purchaseOrderDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id: " + purchaseOrderDTO.getSupplierId()));
            existingOrder.setSupplier(supplier);
        }

        existingOrder.setExpectedDelivery(purchaseOrderDTO.getExpectedDelivery());

        if (purchaseOrderDTO.getOrderLines() != null) {
            purchaseOrderLineRepository.deleteByPurchaseOrderId(id);

            List<PurchaseOrderLine> newOrderLines = purchaseOrderDTO.getOrderLines().stream()
                    .map(lineDTO -> {
                        PurchaseOrderLine line = lineMapper.toEntity(lineDTO);
                        line.setPurchaseOrder(existingOrder);
                        Product product = productRepository.findById(lineDTO.getProductId())
                                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + lineDTO.getProductId()));
                        line.setProduct(product);
                        return line;
                    })
                    .collect(Collectors.toList());
            purchaseOrderLineRepository.saveAll(newOrderLines);
        }

        PurchaseOrder updatedOrder = purchaseOrderRepository.save(existingOrder);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        purchaseOrderLineRepository.deleteByPurchaseOrderId(id);
        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersBySupplier(UUID supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByUser(UUID userId) {
        return purchaseOrderRepository.findByCreatedById(userId)
                .stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderDTO updatePurchaseOrderStatus(UUID id, PurchaseOrderStatus status) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        boolean updateStock = status == PurchaseOrderStatus.RECEIVED;

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.RECEIVED && updateStock) {
            throw new RuntimeException("Cette commande a déjà été reçue, le stock est déjà mis à jour.");
        }

        purchaseOrder.setStatus(status);
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);

        if (updateStock) {
            List<PurchaseOrderLine> orderLines = purchaseOrder.getOrderLines();
            for (PurchaseOrderLine line : orderLines) {
                List<Inventory> inventories = inventoryRepository.findByProductId(line.getProduct().getId());
                if (inventories == null || inventories.isEmpty()) {
                    throw new RuntimeException("Inventaire non trouvé pour le produit: " + line.getProduct().getId());
                }
                for (Inventory inventory : inventories) {
                    inventory.setQtyOnHand(inventory.getQtyOnHand() + line.getQuantity());
                    inventoryRepository.save(inventory);
                }
            }
        }

        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO approvePurchaseOrder(UUID id, UUID approvedByUserId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        User approvedBy = userRepository.findById(approvedByUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + approvedByUserId));

        purchaseOrder.setApprovedBy(approvedBy);
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);

        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public BigDecimal calculateOrderTotal(UUID purchaseOrderId) {
        List<PurchaseOrderLine> orderLines = purchaseOrderLineRepository.findByPurchaseOrderId(purchaseOrderId);
        return orderLines.stream()
                .map(PurchaseOrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
