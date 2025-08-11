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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper mapper;

    public EmployeeResponseDto addEmployee(EmployeeRequestDto employeeRequestDto) {
        log.debug("Начало создания сотрудника: {}", employeeRequestDto);
        EmployeeEntity newEntity = mapper.toEntity(employeeRequestDto);
        EmployeeEntity savedEntity = employeeRepository.save(newEntity);
        log.info("Сотрудник успешно создан: ID={}, Имя={}", savedEntity.getId(),
                savedEntity.getFirstName() + " " + savedEntity.getLastName());
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
        log.debug("Поиск сотрудников по фильтрам: firstName={}, lastName={}, emailLike={}, page={}",
                firstName, lastName, emailLike, pageable.getPageNumber());
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
        EmployeeEntity entity = employeeRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Сотрудник с ID: {} не найден", id);
                    return new ResourceNotFoundException("Сотрудник с id: " + id + " не найден");
                });
        return mapper.toResponseDto(entity);
    }

    public Long removeEmployee(Long id) {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Сотрудник с ID: {} не найден", id);
                    return new ResourceNotFoundException("Сотрудник с id: " + id + " не найден");
                });
        log.info("Удаление сотрудника с ID: {}", id);
        employeeRepository.delete(employeeEntity);
        log.info("Сотрудник с ID: {} успешно удален", id);
        return employeeEntity.getId();
    }

    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        EmployeeEntity entity = employeeRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Клиент с ID: {} не найден", id);
                    return new ResourceNotFoundException("Клиент с id: " + id + " не найден");
                });
        log.debug("Начало обновления данных сотрудника с ID: {}", id);
        boolean updated = false;

        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
            log.debug("Обновлено имя: {}",  dto.getFirstName());
            updated = true;
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
            log.debug("Обновлена фамилия: {}",  dto.getLastName());
            updated = true;
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(entity.getEmail())) {
            entity.setEmail(dto.getEmail());
            log.debug("Обновлена почта: {}",  dto.getEmail());
            updated = true;
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(dto.getPassword());
            log.debug("Обновлен пароль: {}",  dto.getPassword());
            updated = true;
        }
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
            log.debug("Обновлена должность: {}",  dto.getRole());
            updated = true;
        }
        if (!updated) {
            log.debug("Ни одно поле не было изменено");
            return mapper.toResponseDto(entity);
        }
        EmployeeEntity updatedEntity = employeeRepository.save(entity);
        log.info("Данные сотрудника с ID: {} успешно обновлены", id);
        return mapper.toResponseDto(updatedEntity);
    }
}
