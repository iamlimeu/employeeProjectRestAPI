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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public CustomerResponseDto addCustomer(CustomerRequestDto customerRequestDto) {
        log.debug("Начало создания клиента: {}", customerRequestDto);
        CustomerEntity newEntity = mapper.toEntity(customerRequestDto);
        CustomerEntity savedEntity = customerRepository.save(newEntity);
        log.info("Клиент успешно создан: ID={}, Имя={}", savedEntity.getId(),
                savedEntity.getFirstName() + " " + savedEntity.getLastName());
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
        log.debug("Поиск клиентов по фильтрам: firstName={}, lastName={}, emailLike={}, page={}",
                firstName, lastName, emailLike, pageable.getPageNumber());
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
                orElseThrow(() -> {
                    log.warn("Клиент с ID: {} не найден", id);
                    return new ResourceNotFoundException("Клиент с id: " + id + " не найден");
                });
        return mapper.toResponseDto(customerEntity);
    }

    public Long removeCustomer(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Клиент с ID: {} не найден", id);
                    return new ResourceNotFoundException("Клиент с id: " + id + " не найден");
                });
        log.info("Удаление клиента с ID: {}", id);
        List<OrderEntity> orders = customerEntity.getOrders();
        if (orders != null && !orders.isEmpty()) {
            log.debug("Отвязка клиента от {} заказов", orders.size());
            orders.forEach(order -> order.setCustomer(null));
        }
        customerEntity.getOrders().clear();
        customerRepository.delete(customerEntity);
        log.info("Клиент с ID: {} успешно удален", id);
        return customerEntity.getId();
    }

    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {
        CustomerEntity customerEntity = customerRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Клиент с ID: {} не найден", id);
                    return new ResourceNotFoundException("Клиент с id: " + id + " не найден");
                });
        log.debug("Начало обновления данных клиента с ID: {}", id);
        boolean updated = false;
        if (dto.getFirstName() != null && !dto.getFirstName().equals(customerEntity.getFirstName())) {
            customerEntity.setFirstName(dto.getFirstName());
            log.debug("Обновлено имя: {}",  dto.getFirstName());
            updated = true;
        }
        if (dto.getLastName() != null && !dto.getLastName().equals(customerEntity.getLastName())) {
            customerEntity.setLastName(dto.getLastName());
            log.debug("Обновлена фамилия: {}",  dto.getLastName());
            updated = true;
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(customerEntity.getEmail())) {
            customerEntity.setEmail(dto.getEmail());
            log.debug("Обновлена почта: {}",  dto.getEmail());
            updated = true;
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(customerEntity.getPhoneNumber())) {
            customerEntity.setPhoneNumber(dto.getPhoneNumber());
            log.debug("Обновлен номер телефона: {}",  dto.getPhoneNumber());
            updated = true;
        }
        if (!updated) {
            log.info("Ни одно поле не было изменено для клиента с ID: {}", id);
            return mapper.toResponseDto(customerEntity);
        }
        CustomerEntity updatedEntity = customerRepository.save(customerEntity);
        log.info("Данные клиента с ID: {} успешно обновлены", id);
        return mapper.toResponseDto(updatedEntity);
    }
}
