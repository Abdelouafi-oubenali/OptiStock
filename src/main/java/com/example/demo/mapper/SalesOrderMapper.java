package com.example.demo.mapper;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.entity.SalesOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SalesOrderMapper {

    SalesOrderMapper INSTANCE = Mappers.getMapper(SalesOrderMapper.class);

    @Mapping(source = "user.id", target = "user_id")
    SalesOrderDTO toDTO(SalesOrder entity);

    @Mapping(source = "user_id", target = "user.id")
    SalesOrder toEntity(SalesOrderDTO dto);
}
