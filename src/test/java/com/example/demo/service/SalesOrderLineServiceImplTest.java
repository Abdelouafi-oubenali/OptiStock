package com.example.demo.service;

import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.entity.*;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.repository.*;
import com.example.demo.service.PurchaseOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.demo.service.impl.SalesOrderLineServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SalesOrderLineServiceImplTest {

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;
    @Mock
    private SalesOrderRepository salesOrderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    private SalesOrderLineServiceImpl salesOrderLineService;

    private SalesOrderLineDTO lineDTO;
    private SalesOrder order;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new SalesOrder();
        order.setId(UUID.randomUUID());

        product = new Product();
        product.setId(UUID.randomUUID());

        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyOnHand(10);

        lineDTO = new SalesOrderLineDTO();
        lineDTO.setSales_order_id(order.getId());
        lineDTO.setProduct_id(product.getId());
        lineDTO.setQuantity(5);
        lineDTO.setUnitPrice(BigDecimal.valueOf(50));
    }

    @Test
    void testCreateSalesOrderLine_Success() {
        when(salesOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(product.getId())).thenReturn(List.of(inventory));
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenAnswer(inv -> {
            SalesOrderLine line = inv.getArgument(0);
            line.setId(UUID.randomUUID());
            return line;
        });

        SalesOrderLineDTO result = salesOrderLineService.createSalesOrderLine(lineDTO);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(5);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void testCreateSalesOrderLine_WithBackorder() {
        lineDTO.setQuantity(15); // الطلب أكبر من المخزون (10)

        when(salesOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(product.getId())).thenReturn(List.of(inventory));
        when(supplierRepository.findAll()).thenReturn(List.of(new Supplier()));
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseOrderLineRepository.save(any(PurchaseOrderLine.class))).thenAnswer(inv -> inv.getArgument(0));
        when(salesOrderLineRepository.save(any(SalesOrderLine.class))).thenAnswer(inv -> inv.getArgument(0));

        SalesOrderLineDTO result = salesOrderLineService.createSalesOrderLine(lineDTO);

        assertThat(result).isNotNull();
        assertThat(result.getBackorder()).isEqualTo(5); // car 15 - 10 = 5
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(purchaseOrderLineRepository, times(1)).save(any(PurchaseOrderLine.class));
    }

    @Test
    void testCreateSalesOrderLine_ProductNotFound() {
        when(salesOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                salesOrderLineService.createSalesOrderLine(lineDTO)
        );

        assertThat(ex.getMessage()).contains("Produit introuvable");
    }

    @Test
    void testCreateSalesOrderLine_NoInventory() {
        when(salesOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(product.getId())).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                salesOrderLineService.createSalesOrderLine(lineDTO)
        );

        assertThat(ex.getMessage()).contains("Aucun inventaire trouvé");
    }
}
