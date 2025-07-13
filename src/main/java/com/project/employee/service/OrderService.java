package com.project.employee.service;

import com.project.employee.dto.OrderRequestDto;
import com.project.employee.dto.OrderResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import com.project.employee.enums.OrderStatus;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.OrderMapper;
import com.project.employee.repository.CustomerRepository;
import com.project.employee.repository.OrderRepository;
import com.project.employee.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper mapper;

    public OrderResponseDto addOrder(OrderRequestDto orderRequestDto) {
        CustomerEntity customerEntity = customerRepository.findById(orderRequestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " +
                        orderRequestDto.getCustomerId()));

        OrderEntity newEntity = mapper.toEntity(orderRequestDto);
        newEntity.setOrderStatus(orderRequestDto.getOrderStatus());
        newEntity.setCustomer(customerEntity);
        OrderEntity savedEntity = orderRepository.save(newEntity);
        return mapper.toResponseDto(savedEntity);
    }

    public PageResponse<OrderResponseDto> getAllOrders(
            LocalDateTime createdDate,
            OrderStatus status,
            Pageable pageable
    ) {
        Specification<OrderEntity> specs = OrderSpecification.filter(createdDate, status);
        Page<OrderEntity> page = orderRepository.findAll(specs, pageable);
        Page<OrderResponseDto> dtoPage = page.map(mapper::toResponseDto);
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

    public OrderResponseDto getOrderById(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order with id: " + id + " not found"));
        return mapper.toResponseDto(orderEntity);
    }

    public Long removeOrder(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order with id: " + id + " not found"));
        orderRepository.delete(orderEntity);
        return orderEntity.getId();
    }

    public OrderResponseDto updateOrder(Long id, OrderRequestDto dto) {
        OrderEntity entity = orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order with id: " + id + " not found"));
        if (entity.getOrderStatus() != null) {
            entity.setOrderStatus(dto.getOrderStatus());
        }
        OrderEntity updatedOrder = orderRepository.save(entity);
        return mapper.toResponseDto(updatedOrder);
    }
}
