package com.project.employee.dto;

import com.project.employee.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "DTO для создания или обновления заказа")
public class OrderRequestDto {

    @Schema(
            description = "Статус заказа",
            example = "Новый",
            allowableValues = "Новый, В обработке, Завершен или Отменен",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Статус заказа не должен быть пустым")
    private OrderStatus orderStatus;

    private Long customerId;
}
