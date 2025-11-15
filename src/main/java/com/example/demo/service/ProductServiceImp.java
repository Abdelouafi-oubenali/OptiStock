package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    private final ProductMapper mapper = ProductMapper.INSTANCE;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new RuntimeException("Un produit avec ce SKU existe déjà: " + productDTO.getSku());
        }

        Product product = mapper.toEntity(productDTO);
        Product saved = productRepository.save(product);
        return mapper.toDTO(saved);
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));
        return mapper.toDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProduct(UUID id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));

        if (!existingProduct.getSku().equals(productDTO.getSku()) &&
                productRepository.existsBySku(productDTO.getSku())) {
            throw new RuntimeException("Un autre produit avec ce SKU existe déjà: " + productDTO.getSku());
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setSku(productDTO.getSku());
        existingProduct.setPrice(productDTO.getPrice());

        Product updated = productRepository.save(existingProduct);
        return mapper.toDTO(updated);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));

        if (product.getInventories() != null && !product.getInventories().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit: il existe des inventaires associés");
        }
        if (product.getPurchaseOrderLines() != null && !product.getPurchaseOrderLines().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit: il existe des lignes de commande d'achat associées");
        }
        if (product.getSalesOrderLines() != null && !product.getSalesOrderLines().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit: il existe des lignes de commande de vente associées");
        }

        productRepository.delete(product);
    }

    @Override
    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le SKU: " + sku));
        return mapper.toDTO(product);
    }

    @Override
    public void updateStatusProduct(UUID uuid) {
        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        List<Inventory> productStock = inventoryRepository.findByProductId(uuid);
        String newStatus = "HIDINE";

        if (productStock == null || productStock.isEmpty()) {
            if ("CREATED".equals(product.getStatus()) || "RESERVED".equals(product.getStatus())) {
                product.setStatus(newStatus);
                productRepository.save(product);
            } else {
                throw new RuntimeException("Le produit est déjà réservé, impossible de changer ce status");
            }
        } else {
            throw new RuntimeException("Le produit est déjà réservé, impossible de changer ce status");
        }
    }
}
