package com.project.employee.controller;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.enums.EmployeeRole;
import com.project.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employees", description = "API для управления сотрудниками")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/employees")
    @Operation(summary = "Создать нового сотрудника", description = "Позволяет создать нового сотрудника")
    public ResponseEntity<EmployeeResponseDto> addEmployee(@Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        log.info("Получен запрос на создание сотрудника: {} {}", employeeRequestDto.getFirstName(),
                                                                employeeRequestDto.getLastName());
        EmployeeResponseDto newEmployeeEntity = employeeService.addEmployee(employeeRequestDto);
        log.info("Сотрудник с ID: {} успешно создан", newEmployeeEntity.getId());
        return new ResponseEntity<>(newEmployeeEntity, HttpStatus.CREATED);
    }

    @GetMapping("/employees")
    @Operation(summary = "Получить всех сотрудников", description = "Возвращает список всех сотрудников")
    public ResponseEntity<PageResponse<EmployeeResponseDto>> getEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) EmployeeRole role,
            @RequestParam(required = false) String emailLike,
            @PageableDefault(page = 0, size = 10, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable
            ) {
        log.info("Получение всех сотрудников");
        PageResponse<EmployeeResponseDto> response = employeeService.getEmployees(
                firstName, lastName, role, emailLike, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "Получить информацию о сотруднике",
            description = "Позволяет получить информацию о сотруднику по его ID")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") Long id) {
        log.info("Получение сотрудника по ID: {}", id);
        return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
    }

    @DeleteMapping("/employees/{id}")
    @Operation(summary = "Удалить сотрудника",
            description = "Позволяет удалить сотрудника по его ID")
    public ResponseEntity<Long> removeEmployee(@PathVariable("id") Long id) {
        log.warn("Запрос на удаление сотрудника с ID: {}", id);
        Long removedEmployeeId = employeeService.removeEmployee(id);
        log.info("Сотрудник с ID {} успешно удален", id);
        return ResponseEntity.ok(removedEmployeeId);
    }

    @PatchMapping("/employees/{id}")
    @Operation(summary = "Обновить информацию о сотруднике",
            description = "Позволяет обновить информацию о сотруднику по его ID")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable("id") Long id,
                                                              @Valid
                                                              @RequestBody EmployeeRequestDto employeeRequestDto) {
        log.warn("Запрос на изменение данных сотрудника с ID: {}", id);
        EmployeeResponseDto updatedEmployeeEntity = employeeService.updateEmployee(id, employeeRequestDto);
        log.info("Данные сотрудника с ID {} успешно изменены", id);
        return new ResponseEntity<>(updatedEmployeeEntity, HttpStatus.OK);
    }
}
