package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImp productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySku("SKU123")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(product.getId());

        assertEquals(product.getName(), result.getName());
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(UUID.randomUUID()));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Laptop Dell", result.get(0).getName());
    }

    @Test
    void testGetProductBySku_Success() {
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductBySku("SKU123");

        assertEquals("Laptop Dell", result.getName());
    }

    @Test
    void testGetProductBySku_NotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductBySku("UNKNOWN"));
    }
}
