package com.project.employee.enums;

public enum OrderStatus {
    NEW("Новый"),
    PROCESSING("В обработке"),
    COMPLETED("Завершен"),
    CANCELED("Отменен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
}
