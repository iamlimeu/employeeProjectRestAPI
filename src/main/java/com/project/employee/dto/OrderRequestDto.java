package com.project.employee.dto;

import com.project.employee.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class OrderRequestDto {
    @NotNull(message = "Order status cannot be null")
    private OrderStatus orderStatus;

    private Long customerId;
}
