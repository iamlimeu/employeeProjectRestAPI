package com.project.employee.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private String info;
    private CustomerInfo customerInfo;
    private List<ProductInfo> productInfo;

    @Data
    public static class CustomerInfo {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
    }

    @Data
    public static class ProductInfo {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
    }
}
