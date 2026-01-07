package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // ========== TEST ENDPOINT FOR ELK ==========
    @GetMapping("/test/log/{id}")
    public ResponseEntity<String> testLogging(@PathVariable String id) {
        log.info("===== ELK TEST INFO: Received ID {} =====", id);
        log.debug("===== ELK TEST DEBUG: Received ID {} =====", id);
        log.error("===== ELK TEST ERROR: Received ID {} =====", id);
        log.warn("===== ELK TEST WARN: Received ID {} =====", id);

        return ResponseEntity.ok("Logs sent for ID: " + id);
    }

    @GetMapping("/test/elk")
    public ResponseEntity<String> testELK() {
        log.info("========== ELK INTEGRATION TEST ==========");
        log.error("========== ELK ERROR TEST ==========");
        log.warn("========== ELK WARNING TEST ==========");
        log.debug("========== ELK DEBUG TEST ==========");

        return ResponseEntity.ok("ELK test logs sent successfully");
    }
    // ===========================================

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        log.info("Getting product by ID: {}", id);
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        ProductDTO product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id) {
        productService.updateStatusProduct(id);
        return ResponseEntity.noContent().build();
    }
}