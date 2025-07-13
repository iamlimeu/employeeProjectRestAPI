package com.project.employee.service;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.entity.EmployeeEntity;
import com.project.employee.enums.EmployeeRole;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.EmployeeMapper;
import com.project.employee.repository.EmployeeRepository;
import com.project.employee.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

    public PageResponse<EmployeeResponseDto> getEmployees(
            String firstName,
            String lastName,
            EmployeeRole role,
            String emailLike,
            Pageable pageable
    ) {
        Specification<EmployeeEntity> specs = EmployeeSpecification.filter(firstName, lastName, emailLike, role);
        Page<EmployeeEntity> pageEntity = employeeRepository.findAll(specs, pageable);
        Page<EmployeeResponseDto> dtoPage = pageEntity.map(mapper::toResponseDto);
        return toPageResponse(dtoPage);
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        var response = new PageResponse<T>();
        response.setContent(page.getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }

    public EmployeeResponseDto getEmployeeById(Long id) {
        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapper.toResponseDto(entity);
    }

    public Long removeEmployee(Long id) {
        EmployeeEntity employeeEntity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employeeEntity);
        return employeeEntity.getId();
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
