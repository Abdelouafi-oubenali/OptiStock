package com.example.demo.mapper;

import com.example.demo.dto.PurchaseOrderLineDTO;
import com.example.demo.entity.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchaseOrderLineMapper {

    PurchaseOrderLineMapper INSTANCE = Mappers.getMapper(PurchaseOrderLineMapper.class);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "productSku")
    PurchaseOrderLineDTO toDTO(PurchaseOrderLine line);

    PurchaseOrderLine toEntity(PurchaseOrderLineDTO lineDTO);
}
