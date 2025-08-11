package com.project.employee.controller;

import com.project.employee.dto.PageResponse;
import com.project.employee.dto.ProductRequestDto;
import com.project.employee.dto.ProductResponseDto;
import com.project.employee.service.ProductService;
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

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "API для управления товарами")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products")
    @Operation(summary = "Создать товар", description = "Позволяет создать товар")
    public ResponseEntity<ProductResponseDto> addProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        log.info("Получен запрос на создание товара: {}", requestDto.getName());
        ProductResponseDto responseDto = productService.addProduct(requestDto);
        log.info("Товар с ID: {} успешно создан", responseDto.getId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров")
    public ResponseEntity<PageResponse<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false)BigDecimal price,
            @PageableDefault(page = 0, size = 10, sort = {"name", "price"}, direction = Sort.Direction.ASC)
            Pageable pageable
            ) {
        log.info("Получение всех товаров");
        PageResponse<ProductResponseDto> responseDto = productService.getAllProducts(
                name, description, price, pageable
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Получить товар по его id", description = "Возвращает информацию о товаре по его id")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") Long id) {
        log.info("Получение товара по ID: {}", id);
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Удалить товар по его id", description = "Удаляет товар по его id")
    public ResponseEntity<Long> removeProduct(@PathVariable("id") long id) {
        log.warn("Запрос на удаление товара с ID: {}", id);
        Long removedProduct = productService.removeProductById(id);
        log.info("Товар с ID: {} успешно удален", id);
        return ResponseEntity.ok(removedProduct);
    }

    @PatchMapping("/products/{id}")
    @Operation(summary = "Обновить товар по его id", description = "Обновляет информацию о товарое по его id")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long id,
                                                            @Valid @RequestBody ProductRequestDto requestDto) {
        log.warn("Запрос на изменение данных товара с ID: {}", id);
        ProductResponseDto responseDto = productService.updateProduct(id, requestDto);
        log.info("Данные товара с ID: {} успешно изменены", id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
