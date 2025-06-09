package com.project.employee.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDto {
    @Min(value = 1, message = "id must be greater than 0")
    private int id;
    private String info;
}
