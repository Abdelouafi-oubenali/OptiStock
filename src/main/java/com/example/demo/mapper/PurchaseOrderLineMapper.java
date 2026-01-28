package com.example.demo.mapper;

import com.example.demo.dto.PurchaseOrderLineDTO;
import com.example.demo.entity.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "productSku")
    PurchaseOrderLineDTO toDTO(PurchaseOrderLine line);

    @Mapping(target = "purchaseOrder", ignore = true)
    PurchaseOrderLine toEntity(PurchaseOrderLineDTO lineDTO);
}
