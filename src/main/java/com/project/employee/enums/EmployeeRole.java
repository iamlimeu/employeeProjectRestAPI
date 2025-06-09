package com.project.employee.enums;

import lombok.Getter;

@Getter
public enum EmployeeRole {
    MANAGER("Менеджер"),
    ADMIN("Администратор");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }
}
