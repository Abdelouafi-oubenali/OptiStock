package com.example.demo.service.impl;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderLineDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseOrderServiceImplTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    private Supplier supplier;
    private User user;
    private Product product;
    private PurchaseOrder purchaseOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = new Supplier();
        supplier.setId(UUID.randomUUID());
        supplier.setName("Supplier A");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("User A");

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Product A");
        product.setSku("SKU123");

        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(UUID.randomUUID());
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setCreatedBy(user);
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
        purchaseOrder.setExpectedDelivery(LocalDate.now().plusDays(3).atStartOfDay());
    }

    @Test
    void testCreatePurchaseOrder_Success() {
        PurchaseOrderLineDTO lineDTO = new PurchaseOrderLineDTO();
        lineDTO.setProductId(product.getId());
        lineDTO.setQuantity(5);
        lineDTO.setUnitPrice(BigDecimal.valueOf(10));

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(supplier.getId());
        dto.setCreatedByUserId(user.getId());
        dto.setExpectedDelivery(LocalDate.now().plusDays(3).atStartOfDay());
        dto.setOrderLines(List.of(lineDTO));

        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        PurchaseOrderDTO result = purchaseOrderService.createPurchaseOrder(dto);

        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.DRAFT, result.getStatus());
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(purchaseOrderLineRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetPurchaseOrderById_NotFound() {
        UUID id = UUID.randomUUID();
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseOrderService.getPurchaseOrderById(id));

        assertEquals("Commande d'achat non trouvée avec l'id: " + id, exception.getMessage());
    }

    @Test
    void testUpdatePurchaseOrderStatus_Received_UpdatesInventory() {
        purchaseOrder.setOrderLines(new ArrayList<>());
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setProduct(product);
        line.setQuantity(10);
        line.setUnitPrice(BigDecimal.valueOf(5));
        purchaseOrder.getOrderLines().add(line);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyOnHand(20);

        when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
        when(inventoryRepository.findByProductId(product.getId())).thenReturn(List.of(inventory));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

        PurchaseOrderDTO result = purchaseOrderService.updatePurchaseOrderStatus(purchaseOrder.getId(), PurchaseOrderStatus.RECEIVED);

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        assertEquals(PurchaseOrderStatus.RECEIVED, result.getStatus());
        assertEquals(30, inventory.getQtyOnHand());
    }
}
