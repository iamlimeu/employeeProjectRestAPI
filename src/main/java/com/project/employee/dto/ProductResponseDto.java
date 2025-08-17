package com.project.employee.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private Long  id;
    private String name;
    private String description;
    private BigDecimal price;
}
