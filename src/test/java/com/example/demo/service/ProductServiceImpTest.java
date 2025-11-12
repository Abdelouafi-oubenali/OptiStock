package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImp productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {

        UUID id = UUID.randomUUID();

        product = Product.builder()
                .id(id)
                .name("Laptop Dell")
                .description("High performance laptop")
                .sku("SKU123")
                .price(new BigDecimal("1200.6"))
                .build();

        productDTO = ProductDTO.builder()
                .id(id)
                .name("Laptop Dell")
                .description("High performance laptop")
                .sku("SKU123")
                .price(new BigDecimal("1200.0"))
                .build();
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.existsBySku("SKU123")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals("Laptop Dell", result.getName());
        assertEquals("SKU123", result.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productRepository, times(1)).existsBySku("SKU123");
    }

    @Test
    void testCreateProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySku("SKU123")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.createProduct(productDTO));

        assertTrue(exception.getMessage().contains("SKU"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(product.getId());

        assertNotNull(result);
        assertEquals("Laptop Dell", result.getName());
        assertEquals("SKU123", result.getSku());
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductById(randomId));

        assertTrue(exception.getMessage().contains("non trouvé"));
        verify(productRepository, times(1)).findById(randomId);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop Dell", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductBySku_Success() {
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductBySku("SKU123");

        assertNotNull(result);
        assertEquals("Laptop Dell", result.getName());
        assertEquals("SKU123", result.getSku());
        verify(productRepository, times(1)).findBySku("SKU123");
    }

    @Test
    void testGetProductBySku_NotFound() {
        when(productRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductBySku("UNKNOWN"));

        assertTrue(exception.getMessage().contains("non trouvé"));
        verify(productRepository, times(1)).findBySku("UNKNOWN");
    }
}