package com.example.demo.mapper;

import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.entity.SalesOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SalesOrderLineMapper {

    SalesOrderLineMapper INSTANCE = Mappers.getMapper(SalesOrderLineMapper.class);

    @Mapping(source = "salesOrder.id", target = "sales_order_id")
    @Mapping(source = "product.id", target = "product_id")
    SalesOrderLineDTO toDTO(SalesOrderLine entity);

    @Mapping(source = "sales_order_id", target = "salesOrder.id")
    @Mapping(source = "product_id", target = "product.id")
    SalesOrderLine toEntity(SalesOrderLineDTO dto);
}
