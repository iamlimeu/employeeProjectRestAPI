package com.project.employee.dto;

import com.project.employee.enums.EmployeeRole;
import lombok.Data;

@Data
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private EmployeeRole role;
}
