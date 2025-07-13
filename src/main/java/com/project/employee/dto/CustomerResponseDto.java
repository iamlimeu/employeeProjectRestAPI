package com.project.employee.dto;

import com.project.employee.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerResponseDto {
    private Long id;
    private String info;
    private List<OrderInfo> orders;


    @Data
    public static class OrderInfo {
        private Long id;
        @NotNull(message = "Order status cannot be null")
        private OrderStatus status;
        private LocalDateTime createdDate;
    }
}
