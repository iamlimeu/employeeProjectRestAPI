package com.project.employee.controller;

import com.project.employee.dto.OrderRequestDto;
import com.project.employee.dto.OrderResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.entity.ProductEntity;
import com.project.employee.enums.OrderStatus;
import com.project.employee.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> addOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto responseDto = orderService.addOrder(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/orders/{orderId}/products/{productId}")
    public ResponseEntity<OrderResponseDto> addProductToOrder(@PathVariable("orderId") Long orderId,
                                                              @PathVariable("productId") long productId) {
        OrderResponseDto responseDto = orderService.addProductToOrder(orderId, productId);
        return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/orders")
    public ResponseEntity<PageResponse<OrderResponseDto>> getAllOrders(
            @RequestParam(required = false) LocalDateTime createdDate,
            @RequestParam(required = false, name = "orderStatus") OrderStatus orderStatus,
            @RequestParam(required = false) Long productId,
            @PageableDefault(page = 0, size = 10, sort = "createdDate")
            Pageable pageable
            ) {
        PageResponse<OrderResponseDto> response = orderService.getAllOrders(
                createdDate,
                orderStatus,
                productId,
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Long> removeOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.removeOrder(id));
    }

    @DeleteMapping("/orders/{orderId}/products/{productId}")
    public ResponseEntity<List<ProductEntity>> removeProductInOrder(@PathVariable("orderId") Long orderId,
                                                                   @PathVariable("productId") long productId) {
        return ResponseEntity.ok(orderService.removeProductInOrder(orderId, productId));
    }

    @PatchMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable("id") Long id,
                                                        @Valid @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto updatedOrder = orderService.updateOrder(id, requestDto);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
}
