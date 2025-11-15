package com.example.demo.mapper;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WarehouseMapper {

    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    WarehouseDTO toDTO(Warehouse entity);

    Warehouse toEntity(WarehouseDTO dto);
}
