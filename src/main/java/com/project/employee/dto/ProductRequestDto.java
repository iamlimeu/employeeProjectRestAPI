package com.project.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для создания или обновления товара")
public class ProductRequestDto {


    @Schema(
            description = "Название товара",
            example = "Печенье Oreo",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Название не должно быть пустым")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @Schema(
            description = "Описание товара",
            example = "Хрустящее, сладкое и вкусное печенье, которое идеально сочетается с молоком",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 1000, message = "Максимальная длина описания товара 1000 символов")
    private String description;

    @Schema(
            description = "Стоимость товара в рублях",
            example = "150.00",
            type = "String",
            format = "decimal",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @DecimalMin(value = "0.00", message = "Цена товара не должна быть отрицательной")
    @Digits(integer = 9, fraction = 2, message = "Цена должная быть до 9 целочисленных и 2 дробных чисел")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}
