package com.project.employee.service;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.entity.EmployeeEntity;
import com.project.employee.exception.BadRequestException;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.EmployeeMapper;
import com.project.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.project.employee.mappers.EmployeeMapper.mapToEntity;
import static com.project.employee.mappers.EmployeeMapper.mapToResponseDto;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto) {
        EmployeeEntity newEntity = mapToEntity(employeeRequestDto);
        EmployeeEntity employeeEntity = employeeRepository.addEmployee(newEntity);
        return mapToResponseDto(employeeEntity);
    }

    public List<EmployeeResponseDto> getAllEmployees() {
        if (employeeRepository.getAllEmployees().isEmpty()) {
            throw new ResourceNotFoundException("Not single employee created yet");
        }
        return employeeRepository.getAllEmployees().stream()
                .map(EmployeeMapper::mapToResponseDto)
                .toList();
    }

    public EmployeeResponseDto getEmployeeById(int id) {
        Optional<EmployeeEntity> optionalEmployee = employeeRepository.getEmployeeById(id);
        if (optionalEmployee.isEmpty()) {
            throw new ResourceNotFoundException("Employee with id " + id + " does not exist.");
        }
        return mapToResponseDto(optionalEmployee.get());
    }

    public boolean removeEmployee(int id) {
        if (employeeRepository.getEmployeeById(id).isEmpty()) {
            throw new BadRequestException("There is already no employee with id: " + id);
        }
        return employeeRepository.removeEmployee(id);
    }

    public EmployeeResponseDto updateEmployee(int id, EmployeeRequestDto updatedEmployeeEntity) {
        EmployeeEntity employeeEntity = mapToEntity(updatedEmployeeEntity);
        if (updatedEmployeeEntity.getFirstName() != null) {
            employeeEntity.setFirstName(updatedEmployeeEntity.getFirstName());
        }
        if (updatedEmployeeEntity.getLastName() != null) {
            employeeEntity.setLastName(updatedEmployeeEntity.getLastName());
        }
        if (updatedEmployeeEntity.getEmail() != null) {
            employeeEntity.setEmail(updatedEmployeeEntity.getEmail());
        }
        if (updatedEmployeeEntity.getPassword() != null && !updatedEmployeeEntity.getPassword().isEmpty()) {
            employeeEntity.setPassword(updatedEmployeeEntity.getPassword());
        }
        if (updatedEmployeeEntity.getRole() != null) {
            employeeEntity.setRole(updatedEmployeeEntity.getRole());
        }
        return mapToResponseDto(employeeRepository.updateEmployee(id, employeeEntity));
    }
}
