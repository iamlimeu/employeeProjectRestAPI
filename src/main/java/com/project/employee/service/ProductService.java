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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductResponseDto addProduct(ProductRequestDto dto) {
        ProductEntity entity = mapper.toEntity(dto);
        ProductEntity savedEntity = productRepository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }

    public PageResponse<ProductResponseDto> getAllProducts(
            String name,
            String description,
            BigDecimal price,
            Pageable pageable
    ) {
        Specification<ProductEntity> specs = ProductSpecification.filter(name, description, price);
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
                orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + " not found"));
        return mapper.toResponseDto(entity);
    }

    public Long removeProductById(Long id) {
        ProductEntity entity = productRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + " not found"));
        if (!entity.getOrders().isEmpty()) {
            throw new IllegalStateException("Cannot delete product with id " + id +
                                            " because it is associated with order");
        }
        productRepository.delete(entity);
        return entity.getId();
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        ProductEntity productEntity = productRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + " not found"));
        if (dto.getName() != null) {
            productEntity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            productEntity.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            productEntity.setPrice(dto.getPrice());
        }
        ProductEntity updatedEntity = productRepository.save(productEntity);
        return mapper.toResponseDto(updatedEntity);
    }

}
