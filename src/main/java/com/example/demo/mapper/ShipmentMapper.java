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
    @Mapping(source = "carrier.id", target = "carrierId")
    ShipmentDTO toDTO(Shipment entity);

    @Mapping(source = "salesOrderId", target = "salesOrder.id")
    @Mapping(source = "carrierId", target = "carrier.id")
    Shipment toEntity(ShipmentDTO dto);
}
