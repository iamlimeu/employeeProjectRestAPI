package com.project.employee.service;

import com.project.employee.dto.CustomerRequestDto;
import com.project.employee.dto.CustomerResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.CustomerMapper;
import com.project.employee.repository.CustomerRepository;
import com.project.employee.specification.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public CustomerResponseDto addCustomer(CustomerRequestDto customerRequestDto) {
        CustomerEntity newEntity = mapper.toEntity(customerRequestDto);
        CustomerEntity savedEntity = customerRepository.save(newEntity);
        return mapper.toResponseDto(savedEntity);
    }

    public PageResponse<CustomerResponseDto> getAllCustomers(
            String firstName,
            String lastName,
            String emailLike,
            String phoneNumber,
            Pageable pageable
    ) {
        Specification<CustomerEntity> specs = CustomerSpecification.
                filter(firstName, lastName, emailLike, phoneNumber);
        Page<CustomerEntity> page = customerRepository.findAll(specs, pageable);
        Page<CustomerResponseDto> dtoPage = page.map(mapper::toResponseDto);
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

    public CustomerResponseDto getCustomerById(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + id + " not found"));
        return mapper.toResponseDto(customerEntity);
    }

    public Long removeCustomer(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + id + " not found"));
        List<OrderEntity> orders = customerEntity.getOrders();
        if (orders != null && !orders.isEmpty()) {
            orders.forEach(order -> order.setCustomer(null));
        }
        customerEntity.getOrders().clear();
        customerRepository.delete(customerEntity);
        return customerEntity.getId();
    }

    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {
        CustomerEntity customerEntity = customerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Customer with id: " + id + " not found"));
        if (dto.getFirstName() != null) {
            customerEntity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            customerEntity.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            customerEntity.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            customerEntity.setPhoneNumber(dto.getPhoneNumber());
        }
        CustomerEntity updatedEntity = customerRepository.save(customerEntity);
        return mapper.toResponseDto(updatedEntity);
    }
}
