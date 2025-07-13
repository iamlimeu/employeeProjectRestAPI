package com.project.employee.dto;

import lombok.Data;

@Data
public class OrderResponseDto {
    private Long id;
    private String info;
    private CustomerInfo customerInfo;

    @Data
    public static class CustomerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
    }
}
