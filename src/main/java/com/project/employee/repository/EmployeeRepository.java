package com.project.employee.repository;

import com.project.employee.entity.EmployeeEntity;
import com.project.employee.exception.BadRequestException;
import com.project.employee.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
    private final List<EmployeeEntity> employeeEntities = new ArrayList<>();
    private int id = 1;

    public EmployeeEntity addEmployee(EmployeeEntity employeeEntity) {
        boolean emailExists = employeeEntities.stream()
                .anyMatch(employee -> employee.getEmail().equals(employeeEntity.getEmail()));
        if (emailExists) {
            throw new BadRequestException("Email: " + employeeEntity.getEmail() + " already exists");
        }
        employeeEntity.setId(id);
        employeeEntities.add(employeeEntity);
        id++;
        return employeeEntity;
    }

    public List<EmployeeEntity> getAllEmployees() {
        return employeeEntities;
    }

    public Optional<EmployeeEntity> getEmployeeById(int id) {
        return employeeEntities.stream()
                .filter(employee -> employee.getId() == id)
                .findFirst();
    }

    public boolean removeEmployee(int id) {
        Optional<EmployeeEntity> optionalEmployee = this.getEmployeeById(id);
        if (optionalEmployee.isEmpty()) {
            throw new IllegalArgumentException("Employee with id " + id + " does not exist.");
        }
        return employeeEntities.remove(optionalEmployee.get());
    }

    public EmployeeEntity updateEmployee(int id, EmployeeEntity updatedEmployeeEntity) {
        Optional<EmployeeEntity> optionalEmployee = this.getEmployeeById(id);
        if (optionalEmployee.isEmpty()) {
            throw new ResourceNotFoundException("Employee with id " + id + " does not exist.");
        }
        EmployeeEntity employeeEntity = optionalEmployee.get();
        employeeEntity.setFirstName(updatedEmployeeEntity.getFirstName());
        employeeEntity.setLastName(updatedEmployeeEntity.getLastName());
        employeeEntity.setEmail(updatedEmployeeEntity.getEmail());
        employeeEntity.setPassword(updatedEmployeeEntity.getPassword());
        employeeEntity.setRole(updatedEmployeeEntity.getRole());
        return employeeEntity;
    }
}
