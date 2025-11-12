package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository ;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Vérifier si le SKU existe déjà
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new RuntimeException("Un produit avec ce SKU existe déjà: " + productDTO.getSku());
        }

        Product product = toEntity(productDTO);
        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));
        return toDto(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
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
        return toDto(updated);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));

        if (!product.getInventories().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit: il existe des inventaires associés");
        }
        if (!product.getPurchaseOrderLines().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit: il existe des lignes de commande d'achat associées");
        }
        if (!product.getSalesOrderLines().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le produit: il existe des lignes de commande de vente associées");
        }

        productRepository.delete(product);
    }

    @Override
    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le SKU: " + sku));
        return toDto(product);
    }

    @Override
    public void updateStatusProduct(UUID uuid) {
        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        List<Inventory> productStock = inventoryRepository.findByProductId(uuid);
        String newStatus = "Hidine";
        if (productStock == null || productStock.isEmpty()) {
            if (product.getStatus().equals("CREATED") || product.getStatus().equals("RESERVED")) {
                product.setStatus(newStatus);
                productRepository.save(product);
            } else {
                throw new RuntimeException("Le produit est déjà réservé, impossible de changer ce status");
            }
        } else {
            throw new RuntimeException("Le produit est déjà réservé, impossible de changer ce status");
        }
    }


    private Product toEntity(ProductDTO dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .build();
    }

    private ProductDTO toDto(Product entity) {
        if (entity == null) {
            return null ;
        }
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .price(entity.getPrice())
                .build();
    }
}