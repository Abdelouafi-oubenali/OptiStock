package com.example.demo.mapper;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupplierMapper {

    SupplierMapper INSTANCE = Mappers.getMapper(SupplierMapper.class);

    SupplierDTO toDTO(Supplier entity);

    Supplier toEntity(SupplierDTO dto);
}
