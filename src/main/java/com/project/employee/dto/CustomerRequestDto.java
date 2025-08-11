package com.project.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для создания или обновления клиента")
public class CustomerRequestDto {

    @Schema(
            description = "Имя клиента",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String firstName;

    @Schema(
            description = "Фамилия клиента",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Фамилия не должна быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;

    @Schema(
            description = "Email клиента",
            example = "ivan@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Почта не должна быть пустой")
    @Size(min = 8, max = 64, message = "Длина почты должна быть от 8 до 64 символов")
    private String email;

    @Schema(
            description = "Номер телефона (с кодом страны)",
            example = "+7 999 123-45-67",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Номер телефона не должен быть пустым")
    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{7,20}$",
            message = "Номер телефона должен содержать 7-20 цифр. Разрешены пробелы, дефисы или скобки"
    )
    private String phoneNumber;
}
