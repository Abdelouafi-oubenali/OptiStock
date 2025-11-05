package com.example.demo.service.impl;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderLineDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.PurchaseOrderStatus;
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
    private final InventoryRepository inventoryRepository ;

    @Override
    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        // Vérifier le fournisseur
        Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id: " + purchaseOrderDTO.getSupplierId()));

        // Vérifier l'utilisateur créateur
        User createdBy = userRepository.findById(purchaseOrderDTO.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + purchaseOrderDTO.getCreatedByUserId()));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setCreatedBy(createdBy);
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
        purchaseOrder.setExpectedDelivery(purchaseOrderDTO.getExpectedDelivery());

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        // Sauvegarder les lignes de commande
        if (purchaseOrderDTO.getOrderLines() != null && !purchaseOrderDTO.getOrderLines().isEmpty()) {
            List<PurchaseOrderLine> orderLines = purchaseOrderDTO.getOrderLines().stream()
                    .map(lineDTO -> convertToLineEntity(lineDTO, savedOrder))
                    .collect(Collectors.toList());
            purchaseOrderLineRepository.saveAll(orderLines);
        }

        return convertToDTO(savedOrder);
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));
        return convertToDTO(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderDTO updatePurchaseOrder(UUID id, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        // Mettre à jour les champs de base
        if (purchaseOrderDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id: " + purchaseOrderDTO.getSupplierId()));
            existingOrder.setSupplier(supplier);
        }

        existingOrder.setExpectedDelivery(purchaseOrderDTO.getExpectedDelivery());

        // Mettre à jour les lignes de commande
        if (purchaseOrderDTO.getOrderLines() != null) {
            // Supprimer les anciennes lignes
            purchaseOrderLineRepository.deleteByPurchaseOrderId(id);

            // Créer les nouvelles lignes
            List<PurchaseOrderLine> newOrderLines = purchaseOrderDTO.getOrderLines().stream()
                    .map(lineDTO -> convertToLineEntity(lineDTO, existingOrder))
                    .collect(Collectors.toList());
            purchaseOrderLineRepository.saveAll(newOrderLines);
        }

        PurchaseOrder updatedOrder = purchaseOrderRepository.save(existingOrder);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        // Supprimer d'abord les lignes
        purchaseOrderLineRepository.deleteByPurchaseOrderId(id);

        // Puis supprimer la commande
        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersBySupplier(UUID supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // CORRECTION : Ajouter cette méthode manquante
    public List<PurchaseOrderDTO> getPurchaseOrdersByUser(UUID userId) {
        return purchaseOrderRepository.findByCreatedById(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderDTO updatePurchaseOrderStatus(UUID id, PurchaseOrderStatus status) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec l'id: " + id));

        boolean updateStock = status == PurchaseOrderStatus.RECEIVED;

        purchaseOrder.setStatus(status);
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);


        // Mise à jour du stock uniquement si status RECEIVED
        if (updateStock) {

            if (purchaseOrder.getStatus() == PurchaseOrderStatus.RECEIVED) {
                throw new RuntimeException("Cette commande a déjà été reçue, le stock est déjà mis à jour.");
            }
            List<PurchaseOrderLine> orderLines = purchaseOrder.getOrderLines(); // Récupérer les lignes de commande
            for (PurchaseOrderLine line : orderLines) {
                Inventory inventory = inventoryRepository.findByProductId(line.getProduct().getId());
                if (inventory == null) {
                    throw new RuntimeException("Inventaire non trouvé pour le produit: " + line.getProduct().getId());
                }

                inventory.setQtyOnHand(inventory.getQtyOnHand() + line.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        return convertToDTO(updatedOrder);
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
        return convertToDTO(updatedOrder);
    }

    @Override
    public BigDecimal calculateOrderTotal(UUID purchaseOrderId) {
        List<PurchaseOrderLine> orderLines = purchaseOrderLineRepository.findByPurchaseOrderId(purchaseOrderId);
        return orderLines.stream()
                .map(PurchaseOrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PurchaseOrderLine convertToLineEntity(PurchaseOrderLineDTO lineDTO, PurchaseOrder purchaseOrder) {
        // Vérifier le produit
        Product product = productRepository.findById(lineDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + lineDTO.getProductId()));

        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setPurchaseOrder(purchaseOrder);
        line.setProduct(product);
        line.setQuantity(lineDTO.getQuantity());
        line.setUnitPrice(lineDTO.getUnitPrice());

        return line;
    }

    private PurchaseOrderDTO convertToDTO(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderLine> orderLines = purchaseOrderLineRepository.findByPurchaseOrderId(purchaseOrder.getId());

        // Calculer le montant total
        BigDecimal totalAmount = orderLines.stream()
                .map(PurchaseOrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PurchaseOrderLineDTO> lineDTOs = orderLines.stream()
                .map(this::convertLineToDTO)
                .collect(Collectors.toList());

        return PurchaseOrderDTO.builder()
                .id(purchaseOrder.getId())
                .supplierId(purchaseOrder.getSupplier().getId())
                .createdByUserId(purchaseOrder.getCreatedBy().getId())
                .approvedByUserId(purchaseOrder.getApprovedBy() != null ?
                        purchaseOrder.getApprovedBy().getId() : null)
                .status(purchaseOrder.getStatus())
                .createdAt(purchaseOrder.getCreatedAt())
                .expectedDelivery(purchaseOrder.getExpectedDelivery())
                .totalAmount(totalAmount)
                .orderLines(lineDTOs)
                .build();
    }

    private PurchaseOrderLineDTO convertLineToDTO(PurchaseOrderLine line) {
        return PurchaseOrderLineDTO.builder()
                .id(line.getId())
                .productId(line.getProduct().getId())
                .productName(line.getProduct().getName())
                .productSku(line.getProduct().getSku())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .totalPrice(line.getTotalPrice())
                .build();
    }
}