package com.project.employee.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDto {

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000, message = "Description length must be up to 1000 characters")
    private String description;

    @NotNull
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @DecimalMax(value = "100_000_000.00", message = "Price must not exceed 100,000,000.00")
    @Digits(integer = 9, fraction = 2, message = "Price must have up to 9 integer and 2 decimal digits")
    private BigDecimal price;
}
