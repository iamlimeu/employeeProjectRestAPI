package com.project.employee.dto;

import com.project.employee.enums.EmployeeRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeRequestDto {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Size(min = 8, max = 64, message = "Email address length must be between 8 and 64 characters")
    private String email;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, message = "password length must be at least 8 characters")
    private String password;

    @NotNull(message = "Role cannot be null. Role must be either Admin or Manager")
    private EmployeeRole role;
}
