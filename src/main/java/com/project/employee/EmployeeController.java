package com.project.employee;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") int id) {
        return  new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
    }

    @DeleteMapping("/employees/{id}")
    public boolean removeEmployee(@PathVariable("id") int id) {
        return employeeService.removeEmployee(id);
    }

    @PatchMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable("id") int id,
                                                         @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponseDto updatedEmployeeEntity = employeeService.updateEmployee(id, employeeRequestDto);
        return new ResponseEntity<>(updatedEmployeeEntity, HttpStatus.OK);
    }
}
