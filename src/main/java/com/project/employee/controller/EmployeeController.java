package com.project.employee.controller;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.enums.EmployeeRole;
import com.project.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponseDto> addEmployee(@Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponseDto newEmployeeEntity = employeeService.addEmployee(employeeRequestDto);
        return new ResponseEntity<>(newEmployeeEntity, HttpStatus.CREATED);
    }

    @GetMapping("/employees")
    public ResponseEntity<PageResponse<EmployeeResponseDto>> getEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) EmployeeRole role,
            @RequestParam(required = false) String emailLike,
            @PageableDefault(page = 0, size = 10, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable
            ) {
        PageResponse<EmployeeResponseDto> response = employeeService.getEmployees(
                firstName, lastName, role, emailLike, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Long> removeEmployee(@PathVariable("id") Long id) {
        return ResponseEntity.ok(employeeService.removeEmployee(id));
    }

    @PatchMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable("id") Long id,
                                                              @Valid
                                                              @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponseDto updatedEmployeeEntity = employeeService.updateEmployee(id, employeeRequestDto);
        return new ResponseEntity<>(updatedEmployeeEntity, HttpStatus.OK);
    }
}
