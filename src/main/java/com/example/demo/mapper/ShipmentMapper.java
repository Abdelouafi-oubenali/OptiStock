package com.example.demo.mapper;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShipmentMapper {

    ShipmentMapper INSTANCE = Mappers.getMapper(ShipmentMapper.class);

    @Mapping(source = "salesOrder.id", target = "salesOrderId")
    ShipmentDTO toDTO(Shipment entity);

    @Mapping(source = "salesOrderId", target = "salesOrder.id")
    Shipment toEntity(ShipmentDTO dto);
}
