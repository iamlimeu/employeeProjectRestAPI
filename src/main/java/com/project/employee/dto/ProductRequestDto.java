package com.project.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Digits(integer = 9, fraction = 2, message = "Price must have up to 9 integer and 2 decimal digits")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}
