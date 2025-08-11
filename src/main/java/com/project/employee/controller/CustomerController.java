package com.project.employee.controller;

import com.project.employee.dto.CustomerRequestDto;
import com.project.employee.dto.CustomerResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.service.CustomerService;
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
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Клиенты", description = "API для управления клиентами")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customers")
    @Operation(summary = "Добавить клиента", description = "Позволяет добавить нового клиента")
    public ResponseEntity<CustomerResponseDto> addCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        log.info("Получен запрос на создание клиента: {} {}", customerRequestDto.getFirstName(),
                                                                customerRequestDto.getLastName());
        CustomerResponseDto responseDto = customerService.addCustomer(customerRequestDto);
        log.info("Клиент с ID: {} успешно создан", responseDto.getId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/customers")
    @Operation(summary = "Показать всех клиентов", description = "Возвращает список всех клиентов")
    public ResponseEntity<PageResponse<CustomerResponseDto>> getCustomers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String emailLike,
            @RequestParam(required = false) String phoneNumber,
            @PageableDefault(page = 0, size = 10, sort = "firstName", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        log.info("Получение всех клиентов");
        PageResponse<CustomerResponseDto> response = customerService.getAllCustomers(
                firstName, lastName, emailLike, phoneNumber, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{id}")
    @Operation(summary = "Показать клиента по его ID", description = "Возвращает информацию о клиенте по его ID")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("id") Long id) {
        log.info("Получение клиента по ID: {}", id);
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    @Operation(summary = "Удалить клиента по его ID",
                description = "Позволяет удалить клиента по его ID")
    public ResponseEntity<Long> removeCustomer(@PathVariable("id") Long id) {
        log.warn("Запрос на удаление клиента с ID: {}", id);
        long removedCustomer = customerService.removeCustomer(id);
        log.info("Сотрудник с ID {} успешно удален", id);
        return ResponseEntity.ok(removedCustomer);
    }

    @PatchMapping("/customers/{id}")
    @Operation(summary = "Обновить данные клиента по его ID",
                description = "Позволяет удалить клиента по его ID")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable("id") Long id,
                                                              @Valid @RequestBody CustomerRequestDto requestDto) {
        log.warn("Запрос на изменение данных клиента с ID: {}", id);
        CustomerResponseDto updatedCustomerEntity = customerService.updateCustomer(id, requestDto);
        log.info("Данные клиента с ID: {} успешно изменены", id);
        return new ResponseEntity<>(updatedCustomerEntity, HttpStatus.OK);
    }
}
