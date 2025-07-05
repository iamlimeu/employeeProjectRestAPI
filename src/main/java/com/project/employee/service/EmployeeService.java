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

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper mapper;

    public EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto) {
        EmployeeEntity newEntity = mapper.toEntity(employeeRequestDto);
        EmployeeEntity savedEntity = employeeRepository.save(newEntity);
        return mapper.toResponseDto(savedEntity);
    }

    public List<EmployeeResponseDto> getAllEmployees() {
        List<EmployeeEntity> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("Not single employee created yet");
        }
        return employees.stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    public EmployeeResponseDto getEmployeeById(Long id) {
        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapper.toResponseDto(entity);
    }

    public void removeEmployee(Long id) {
        if (employeeRepository.findById(id).isEmpty()) {
            throw new BadRequestException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(entity.getEmail())) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(dto.getPassword());
        }
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
        EmployeeEntity updatedEntity = employeeRepository.save(entity);
        return mapper.toResponseDto(updatedEntity);
    }
}
