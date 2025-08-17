package com.project.employee.service;

import com.project.employee.dto.PageResponse;
import com.project.employee.dto.ProductRequestDto;
import com.project.employee.dto.ProductResponseDto;
import com.project.employee.entity.ProductEntity;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.ProductMapper;
import com.project.employee.repository.ProductRepository;
import com.project.employee.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductResponseDto addProduct(ProductRequestDto dto) {
        log.debug("Начало создания товара: {}", dto);
        ProductEntity entity = mapper.toEntity(dto);
        ProductEntity savedEntity = productRepository.save(entity);
        log.info("Товар успешно создан: ID={}, Имя={}, Цена={}", savedEntity.getId(),
                savedEntity.getName(), savedEntity.getPrice());
        return mapper.toResponseDto(savedEntity);
    }

    public PageResponse<ProductResponseDto> getAllProducts(
            String name,
            String description,
            BigDecimal price,
            Pageable pageable
    ) {
        Specification<ProductEntity> specs = ProductSpecification.filter(name, description, price);
        log.debug("Поиск сотрудников по фильтрам: name={}, description={}, price={}, page={}",
                name, description, price, pageable.getPageNumber());
        Page<ProductEntity> page = productRepository.findAll(specs, pageable);
        Page<ProductResponseDto> dtoPage = page.map(mapper::toResponseDto);
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

    public ProductResponseDto getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Товар с ID: {} не найден", id);
                    return new ResourceNotFoundException("Товар с id: " + id + " не найден");
                });
        return mapper.toResponseDto(entity);
    }

    public Long removeProductById(Long id) {
        ProductEntity entity = productRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Товар с ID: {} не найден", id);
                    return new ResourceNotFoundException("Товар с id: " + id + " не найден");
                });
        if (!entity.getOrders().isEmpty()) {
            log.warn("Нельзя удалить товар с ID={}, потому что он связан с заказом");
            throw new IllegalStateException("Нельзя удалить товар с ID: " + id +
                                            " ,потому что он связан с заказом");
        }
        log.info("Удаление товара с ID: {}", id);
        productRepository.delete(entity);
        log.info("Товар с ID: {} успешно удален", id);
        return entity.getId();
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        ProductEntity productEntity = productRepository.findById(id).
                orElseThrow(() -> {
                    log.warn("Товар с ID: {} не найден", id);
                    return new ResourceNotFoundException("Товар с id: " + id + " не найден");
                });
        log.debug("Начало обновления данных товара с ID: {}", id);
        boolean updated = false;
        if (dto.getName() != null && !dto.getName().equals(productEntity.getName())) {
            productEntity.setName(dto.getName());
            log.debug("Обновлено имя: {}", dto.getName());
            updated = true;
        }
        if (dto.getDescription() != null && !dto.getDescription().equals(productEntity.getDescription())) {
            productEntity.setDescription(dto.getDescription());
            log.debug("Обновлено описание: {}",  dto.getDescription());
            updated = true;
        }
        if (dto.getPrice() != null && !dto.getPrice().equals(productEntity.getPrice())) {
            productEntity.setPrice(dto.getPrice());
            log.debug("Обновлена цена: {}", dto.getPrice());
            updated = true;
        }
        if (!updated) {
            log.info("Ни одно поле не было изменено для товара с ID: {}", id);
            return mapper.toResponseDto(productEntity);
        }
        ProductEntity updatedEntity = productRepository.save(productEntity);
        log.info("Данные товара с ID: {} успешно обновлены", id);
        return mapper.toResponseDto(updatedEntity);
    }
}
