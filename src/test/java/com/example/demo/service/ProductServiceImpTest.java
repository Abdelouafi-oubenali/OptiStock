package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductServiceImp productService;

    private Product product;
    private ProductDTO productDTO;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.fromString("8945a242-f888-4814-bde4-6125127a65d1");

        product = Product.builder()
                .id(productId)
                .name("Laptop Dell")
                .description("High performance laptop")
                .sku("SKU123")
                .price(new BigDecimal("1200.00"))
                .inventories(new ArrayList<>())
                .purchaseOrderLines(new ArrayList<>())
                .salesOrderLines(new ArrayList<>())
                .build();

        productDTO = ProductDTO.builder()
                .id(productId)
                .name("Laptop Dell")
                .description("High performance laptop")
                .sku("SKU123")
                .price(new BigDecimal("1200.00"))
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
        assertEquals(productId, result.getId());
        assertEquals(new BigDecimal("1200.00"), result.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productRepository, times(1)).existsBySku("SKU123");
    }

    @Test
    void testCreateProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySku("SKU123")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.createProduct(productDTO));

        assertEquals("Un produit avec ce SKU existe déjà: SKU123", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(productRepository, times(1)).existsBySku("SKU123");
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("Laptop Dell", result.getName());
        assertEquals("SKU123", result.getSku());
        assertEquals(productId, result.getId());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductById(randomId));

        assertEquals("Produit non trouvé avec l'id: " + randomId, exception.getMessage());
        verify(productRepository, times(1)).findById(randomId);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop Dell", result.get(0).getName());
        assertEquals("SKU123", result.get(0).getSku());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductBySku_Success() {
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductBySku("SKU123");

        assertNotNull(result);
        assertEquals("Laptop Dell", result.getName());
        assertEquals("SKU123", result.getSku());
        assertEquals(productId, result.getId());
        verify(productRepository, times(1)).findBySku("SKU123");
    }

    @Test
    void testGetProductBySku_NotFound() {
        when(productRepository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductBySku("UNKNOWN"));

        assertEquals("Produit non trouvé avec le SKU: UNKNOWN", exception.getMessage());
        verify(productRepository, times(1)).findBySku("UNKNOWN");
    }

    @Test
    void testUpdateProduct_Success() {
        ProductDTO updatedDTO = ProductDTO.builder()
                .id(productId)
                .name("Laptop Dell Updated")
                .description("Updated description")
                .sku("SKU123")
                .price(new BigDecimal("1300.00"))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.updateProduct(productId, updatedDTO);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).existsBySku(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_WithDifferentSku_Success() {
        ProductDTO updatedDTO = ProductDTO.builder()
                .id(productId)
                .name("Laptop Dell Updated")
                .description("Updated description")
                .sku("NEW_SKU")
                .price(new BigDecimal("1300.00"))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsBySku("NEW_SKU")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.updateProduct(productId, updatedDTO);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsBySku("NEW_SKU");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(randomId, productDTO));

        assertEquals("Produit non trouvé avec l'id: " + randomId, exception.getMessage());
        verify(productRepository, times(1)).findById(randomId);
        verify(productRepository, never()).existsBySku(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_DuplicateSku() {
        ProductDTO updatedDTO = ProductDTO.builder()
                .id(productId)
                .name("Laptop Dell Updated")
                .description("Updated description")
                .sku("NEW_SKU")
                .price(new BigDecimal("1300.00"))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsBySku("NEW_SKU")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(productId, updatedDTO));

        assertEquals("Un autre produit avec ce SKU existe déjà: NEW_SKU", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsBySku("NEW_SKU");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(randomId));

        assertEquals("Produit non trouvé avec l'id: " + randomId, exception.getMessage());
        verify(productRepository, times(1)).findById(randomId);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
