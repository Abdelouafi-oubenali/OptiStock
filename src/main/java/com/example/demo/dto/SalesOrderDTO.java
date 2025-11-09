package com.example.demo.dto;

import com.example.demo.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderDTO {
    private UUID id;

    @NotNull(message = "L'id de client est obligatoire !!")
    private UUID user_id;

    @NotNull(message = "Le status de sales order est requis !!")
    private OrderStatus orderStatus;



}