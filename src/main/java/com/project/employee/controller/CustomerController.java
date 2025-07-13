package com.project.employee.controller;

import com.project.employee.dto.CustomerRequestDto;
import com.project.employee.dto.CustomerResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponseDto> addCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        CustomerResponseDto responseDto = customerService.addCustomer(customerRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerResponseDto>> getCustomers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String emailLike,
            @RequestParam(required = false) String phoneNumber,
            @PageableDefault(page = 0, size = 10, sort = "firstName", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        PageResponse<CustomerResponseDto> response = customerService.getAllCustomers(
                firstName, lastName, emailLike, phoneNumber, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("id") long id) {
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Long> removeCustomer(@PathVariable("id") long id) {
        return ResponseEntity.ok(customerService.removeCustomer(id));
    }

    @PatchMapping("/customers/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable("id") long id,
                                                              @Valid @RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto updatedCustomerEntity = customerService.updateCustomer(id, requestDto);
        return new ResponseEntity<>(updatedCustomerEntity, HttpStatus.OK);
    }
}
