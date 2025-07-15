package com.project.employee.controller;

import com.project.employee.dto.PageResponse;
import com.project.employee.dto.ProductRequestDto;
import com.project.employee.dto.ProductResponseDto;
import com.project.employee.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> addProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto responseDto = productService.addProduct(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<PageResponse<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false)BigDecimal price,
            @PageableDefault(page = 0, size = 10, sort = {"name", "price"}, direction = Sort.Direction.ASC)
            Pageable pageable
            ) {
        PageResponse<ProductResponseDto> responseDto = productService.getAllProducts(
                name, description, price, pageable
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Long> removeProduct(@PathVariable("id") long id) {
        return ResponseEntity.ok(productService.removeProductById(id));
    }

    @PatchMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") long id,
                                                            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto responseDto = productService.updateProduct(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
