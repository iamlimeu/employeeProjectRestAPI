package com.project.employee.dto;

import com.project.employee.enums.EmployeeRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для создания или обновления сотрудника")
public class EmployeeRequestDto {

    @Schema(
            description = "Имя сотрудника",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String firstName;

    @Schema(
            description = "Фамилия сотрудника",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Фамилия не должна быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;

    @Schema(
            description = "Почта сотрудника",
            example = "ivan@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Почта не должна быть пустой")
    @Size(min = 8, max = 64, message = "Длина почты должна быть от 8 до 64 символов")
    private String email;


    @Schema(
            description = "Пароль сотрудника",
            example = "Qwerty123!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, message = "Длина пароля должная быть минимум 8 символов")
    private String password;

    @Schema(
            description = "Должность сотрудника",
            example = "ADMIN",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Должность сотрудника должна быть ADMIN или MANAGER")
    private EmployeeRole role;
}
