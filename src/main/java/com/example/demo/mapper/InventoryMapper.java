package com.example.demo.mapper;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventoryMapper {

    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    @Mapping(source = "warehouse.id", target = "warehouse_id")
    @Mapping(source = "product.id", target = "product_id")
    InventoryDTO toDTO(Inventory inventory);

    Inventory toEntity(InventoryDTO dto);
}
