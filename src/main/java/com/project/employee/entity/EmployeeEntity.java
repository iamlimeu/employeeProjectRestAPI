package com.project.employee.entity;

import com.project.employee.enums.EmployeeRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EmployeeEntity {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private EmployeeRole role;
}
