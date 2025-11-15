package com.example.demo.mapper;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.entity.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarrierMapper {

    CarrierMapper INSTANCE = Mappers.getMapper(CarrierMapper.class);

    CarrierDTO toDTO(Carrier carrier);

    Carrier toEntity(CarrierDTO carrierDTO);
}
