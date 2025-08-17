package com.project.employee.service;

import com.project.employee.dto.OrderRequestDto;
import com.project.employee.dto.OrderResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import com.project.employee.entity.ProductEntity;
import com.project.employee.enums.OrderStatus;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.OrderMapper;
import com.project.employee.repository.CustomerRepository;
import com.project.employee.repository.OrderRepository;
import com.project.employee.repository.ProductRepository;
import com.project.employee.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper mapper;

    public OrderResponseDto addOrder(OrderRequestDto orderRequestDto) {
        CustomerEntity customerEntity = customerRepository.findById(orderRequestDto.getCustomerId()).
                orElseThrow(() -> {
                    log.warn("Сотрудник с ID: {} не найден", orderRequestDto.getCustomerId());
                    return new ResourceNotFoundException("Сотрудник с id: " +
                            orderRequestDto.getCustomerId() + " не найден");
                });

        log.debug("Начало создания заказа: {}", orderRequestDto);
        OrderEntity newEntity = mapper.toEntity(orderRequestDto);
        newEntity.setOrderStatus(orderRequestDto.getOrderStatus());
        newEntity.setCustomer(customerEntity);
        OrderEntity savedEntity = orderRepository.save(newEntity);
        log.info("Заказ успешно создан: ID={}", savedEntity.getId());
        return mapper.toResponseDto(savedEntity);
    }

    public OrderResponseDto addProductToOrder(Long orderId, Long productId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).
                orElseThrow(() -> {
                    log.warn("Заказ с ID: {} не найден", orderId);
                    return new ResourceNotFoundException("Заказ с id: " + orderId + " не найден");
                });
        ProductEntity productEntity = productRepository.findById(productId).
                orElseThrow(() -> {
                    log.warn("Товар с ID: {} не найден", productId);
                    return new ResourceNotFoundException("Товар с id: " + productId + " не найден");
                });
        log.info("Добавление товара в заказ");
        orderEntity.addProduct(productEntity);
        OrderEntity savedEntity = orderRepository.save(orderEntity);
        log.info("Товар с ID={} успешно добавлен в заказ с ID={}", productId, orderId);
        return mapper.toResponseDto(savedEntity);
    }

    public PageResponse<OrderResponseDto> getAllOrders(
            LocalDateTime createdDate,
            OrderStatus status,
            Long productId,
            Pageable pageable
    ) {
        Specification<OrderEntity> specs = OrderSpecification.filter(createdDate, status, productId);
        log.debug("Поиск заказов по фильтрам: created date={}, status={}, product ID={}, page={}",
                createdDate, status, productId, pageable.getPageNumber());
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
                orElseThrow(() -> {
                    log.warn("Заказ с ID: {} не найден", id);
                    return new ResourceNotFoundException("Заказ с id: " + id + " не найден");
                });
        return mapper.toResponseDto(orderEntity);
    }

    public Long removeOrder(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Заказ с ID: {} не найден", id);
                    return new ResourceNotFoundException("Заказ с id: " + id + " не найден");
                });
        log.info("Удаление заказа с ID: {}", id);
        orderRepository.delete(orderEntity);
        log.info("Заказ с ID: {} успешно удален", id);
        return orderEntity.getId();
    }

    public List<ProductEntity> removeProductInOrder(Long orderId, Long productId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).
                orElseThrow(() -> {
                    log.warn("Заказ с ID: {} не найден", orderId);
                    return new ResourceNotFoundException("Заказ с id: " + orderId + " не найден");
                });
        ProductEntity productEntity = productRepository.findById(productId).
                orElseThrow(() -> {
                    log.warn("Товар с ID: {} не найден", productId);
                    return new ResourceNotFoundException("Товар с id: " + productId + " не найден");
                });
        log.info("Удаление товара из заказа");
        orderEntity.removeProduct(productEntity);
        orderRepository.save(orderEntity);
        log.info("Товар с ID={} успешно удален из заказа с ID={}", productId, orderId);
        return orderEntity.getProducts();
    }

    public OrderResponseDto updateOrder(Long id, OrderRequestDto dto) {
        OrderEntity entity = orderRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Заказ с ID: {} не найден", id);
                    return new ResourceNotFoundException("Заказ с id: " + id + " не найден");
                });
        log.debug("Начало обновления данных заказа с ID: {}", id);
        boolean updated = false;
        if (dto.getOrderStatus() != null && !dto.getOrderStatus().equals(entity.getOrderStatus())) {
            entity.setOrderStatus(dto.getOrderStatus());
            log.debug("Обновлен статус: {}", entity.getOrderStatus());
            updated =true;
        }
        if (!updated) {
            log.info("Ни одно поле не было изменено для заказа с ID: {}", id);
            return mapper.toResponseDto(entity);
        }
        OrderEntity updatedOrder = orderRepository.save(entity);
        log.info("Данные заказа с ID: {} успешно обновлены", id);
        return mapper.toResponseDto(updatedOrder);
    }
}
