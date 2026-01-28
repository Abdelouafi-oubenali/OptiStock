package com.example.demo.mapper;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.entity.PurchaseOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    PurchaseOrderDTO toDTO(PurchaseOrder order);

    PurchaseOrder toEntity(PurchaseOrderDTO orderDTO);
}
