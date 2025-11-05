package com.example.demo.dto;

import com.example.demo.enums.ShipmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentStatusUpdateRequest {
    private ShipmentStatus status;
}