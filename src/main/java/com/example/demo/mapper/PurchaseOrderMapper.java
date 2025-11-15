package com.example.demo.mapper;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.entity.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PurchaseOrderMapper {

    PurchaseOrderMapper INSTANCE = Mappers.getMapper(PurchaseOrderMapper.class);

    PurchaseOrderDTO toDTO(PurchaseOrder order);

    PurchaseOrder toEntity(PurchaseOrderDTO orderDTO);
}
