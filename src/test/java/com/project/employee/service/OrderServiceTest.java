package com.project.employee.service;

import com.project.employee.dto.OrderRequestDto;
import com.project.employee.dto.OrderResponseDto;
import com.project.employee.dto.PageResponse;
import com.project.employee.dto.ProductResponseDto;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import com.project.employee.entity.ProductEntity;
import com.project.employee.enums.OrderStatus;
import com.project.employee.exception.ResourceNotFoundException;
import com.project.employee.mappers.OrderMapper;
import com.project.employee.repository.CustomerRepository;
import com.project.employee.repository.OrderRepository;
import com.project.employee.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class OrderServiceTest {

    private static final Long CUSTOMER_ID = 1L;
    private static final Long ORDER_ID = 21L;
    private static final Long PRODUCT_ID = 44L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper  orderMapper;

    @InjectMocks
    private OrderService orderService;


    private OrderRequestDto requestDto;
    private CustomerEntity customerEntity;
    private ProductEntity productEntity;
    private OrderEntity orderEntity;
    private OrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new OrderRequestDto();
        requestDto.setCustomerId(CUSTOMER_ID);
        requestDto.setOrderStatus(OrderStatus.NEW);

        customerEntity = customer(CUSTOMER_ID, "Evgeny", "Lim",
                "test@gmail.com", "+79281223443");
        productEntity = product(PRODUCT_ID, "MacBook Pro",
                "Apple laptop", BigDecimal.valueOf(100000));

        orderEntity = order(ORDER_ID, OrderStatus.NEW, customerEntity, new ArrayList<>(),
                LocalDateTime.of(2025, 1, 1, 10, 0));


        responseDto = new OrderResponseDto();
        responseDto.setId(ORDER_ID);
        responseDto.setOrderStatus(OrderStatus.NEW);
        responseDto.setCreatedDate(orderEntity.getCreatedDate());
        responseDto.setCustomerInfo(toCustomerInfo(customerEntity));
    }

    private static CustomerEntity customer(long id, String firstName,
                                           String lastName, String email, String phoneNumber) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(id);
        customerEntity.setFirstName(firstName);
        customerEntity.setLastName(lastName);
        customerEntity.setEmail(email);
        customerEntity.setPhoneNumber(phoneNumber);
        customerEntity.setOrders(new ArrayList<>());
        return customerEntity;
    }

    private static ProductEntity product(long id, String name, String description, BigDecimal price) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(id);
        productEntity.setName(name);
        productEntity.setDescription(description);
        productEntity.setPrice(price);
        return productEntity;
    }

    private static OrderEntity order(long id, OrderStatus status, CustomerEntity customer,
                                     List<ProductEntity> products, LocalDateTime created) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setOrderStatus(status);
        orderEntity.setCustomer(customer);
        orderEntity.setProducts(products);
        orderEntity.setCreatedDate(created);
        return orderEntity;
    }

    private static OrderResponseDto.CustomerInfo toCustomerInfo(CustomerEntity customer) {
        OrderResponseDto.CustomerInfo info = new OrderResponseDto.CustomerInfo();
        info.setFirstName(customer.getFirstName());
        info.setLastName(customer.getLastName());
        info.setEmail(customer.getEmail());
        info.setPhoneNumber(customer.getPhoneNumber());
        return info;
    }

    private static OrderRequestDto requestDto(OrderStatus status) {
        OrderRequestDto reqDto = new OrderRequestDto();
        reqDto.setOrderStatus(status);
        return reqDto;
    }

    @Test
    @DisplayName("addOrder: Сохраняет заказ с нужными полями и маппит ответ")
    void addOrder_whenCustomerExists_savesAndReturnResponse() {
        // given
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(orderMapper.toEntity(requestDto)).thenReturn(new OrderEntity());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderMapper.toResponseDto(orderEntity)).thenReturn(responseDto);

        // when
        OrderResponseDto result = orderService.addOrder(requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.NEW);

        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(captor.capture());
        OrderEntity passed = captor.getValue();
        assertThat(passed.getCustomer()).isSameAs(customerEntity);
        assertThat(passed.getOrderStatus()).isEqualTo(OrderStatus.NEW);

        verify(orderMapper).toEntity(requestDto);
        verify(orderMapper).toResponseDto(orderEntity);
        verifyNoMoreInteractions(orderRepository, customerRepository, productRepository, orderMapper);
    }

    @Test
    @DisplayName("addOrder: должен выбросить исключение, если клиент не найден")
    void addOrder_whenCustomerNotFound_throwsException() {
        // given
        requestDto.setCustomerId(123L);
        when(customerRepository.findById(123L)).thenReturn(Optional.empty());

        // when and then
        assertThatThrownBy(() -> orderService.addOrder(requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Сотрудник с id: 123 не найден");

        verify(customerRepository).findById(123L);
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
        verifyNoMoreInteractions(customerRepository, orderRepository, orderMapper, productRepository);
    }

    @Test
    @DisplayName("addProductToOrder: добавляет товар в заказ, если заказ существует и возвращает ответ")
    void addProductToOrder_whenOrderExists_savesAndReturnResponse() {
        // given
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderResponseDto expectedResponseDto = new OrderResponseDto();
        expectedResponseDto.setId(ORDER_ID);
        expectedResponseDto.setOrderStatus(OrderStatus.NEW);
        expectedResponseDto.setCreatedDate(orderEntity.getCreatedDate());
        expectedResponseDto.setCustomerInfo(toCustomerInfo(customerEntity));

        when(orderMapper.toResponseDto(orderEntity)).thenReturn(expectedResponseDto);

        // when
        OrderResponseDto result = orderService.addProductToOrder(ORDER_ID, PRODUCT_ID);

        // then
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(captor.capture());

        assertThat(captor.getValue().getProducts())
                .extracting(ProductEntity::getId)
                        .contains(PRODUCT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(productRepository).findById(PRODUCT_ID);
        verify(orderMapper).toResponseDto(orderEntity);
        verifyNoMoreInteractions(orderRepository, productRepository, orderMapper);
    }

    @Test
    @DisplayName("addProductToOrder: должен выбросить исключение, если заказ не найден")
    void addProductToOrder_whenOrderNotFound_throwsException() {
        // given
        Long notExistedOrderId = 12L;
        when(orderRepository.findById(notExistedOrderId)).thenReturn(Optional.empty());

        // when and then
        assertThatThrownBy(() -> orderService.addProductToOrder(notExistedOrderId, PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Заказ с id: 12 не найден");

        verify(orderRepository).findById(notExistedOrderId);
        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toResponseDto(any());
        verifyNoMoreInteractions(customerRepository, orderRepository, orderMapper, productRepository);
    }

    @Test
    @DisplayName("addProductToOrder: должен выбросить исключение, если товар не найден")
    void addProductToOrder_whenProductNotFound_throwsException() {
        // given
        Long notExistedProductId = 11L;
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));
        when(productRepository.findById(notExistedProductId)).thenReturn(Optional.empty());

        // when and then
        List<Long> before = orderEntity.getProducts().stream().map(ProductEntity::getId).toList();
        assertThatThrownBy(() -> orderService.addProductToOrder(ORDER_ID, notExistedProductId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Товар с id: 11 не найден");

        List<Long> after = orderEntity.getProducts().stream().map(ProductEntity::getId).toList();
        assertThat(after).containsExactlyElementsOf(before);

        verify(orderRepository).findById(ORDER_ID);
        verify(productRepository).findById(notExistedProductId);
        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toResponseDto(any());
        verifyNoMoreInteractions(customerRepository, orderRepository, orderMapper, productRepository);
    }

    @Test
    @DisplayName("getAllOrders: маппит страницу сущностей в корректный PageResponse DTO")
    void getAllOrders_whenOrdersExist_returnPageResponseCorrectly() {
        // given
        Pageable pageable = PageRequest.of(1, 2);

        OrderEntity order1 = new OrderEntity();
        order1.setId(10L);
        OrderEntity order2 = new OrderEntity();
        order2.setId(20L);

        Page<OrderEntity> entityPage = new PageImpl<>(List.of(order1, order2), pageable, 5);
        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(entityPage);

        OrderResponseDto dto1 = new OrderResponseDto();
        dto1.setId(10L);
        OrderResponseDto dto2 = new OrderResponseDto();
        dto2.setId(20L);
        when(orderMapper.toResponseDto(order1)).thenReturn(dto1);
        when(orderMapper.toResponseDto(order2)).thenReturn(dto2);

        // when
        PageResponse<OrderResponseDto> result = orderService.getAllOrders(
                null, OrderStatus.NEW, null, pageable);

        // then
        assertThat(result.getContent()).extracting(OrderResponseDto::getId).containsExactly(10L, 20L);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isFalse();

        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
        verify(orderMapper).toResponseDto(order1);
        verify(orderMapper).toResponseDto(order2);
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("getAllOrders: возвращает пустую страницу корректно( 0 тоталов, first/last = true")
    void getAllOrders_returnEmptyPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

        // when
        PageResponse<OrderResponseDto> result = orderService.getAllOrders(
                null ,null, null, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();

        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
        verifyNoInteractions(orderMapper);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("getOrderById: возвращает найденный заказ, который маппится в DTO")
    void getOrderById_whenOrderExists_returnResponseDto() {
        // given
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toResponseDto(orderEntity)).thenReturn(responseDto);

        // when
        OrderResponseDto result = orderService.getOrderById(ORDER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.NEW);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderMapper).toResponseDto(orderEntity);
        verifyNoMoreInteractions(orderRepository, orderMapper);
        verifyNoInteractions(customerRepository, productRepository);
    }

    @Test
    @DisplayName("getOrderById: должен выбросить исключение, если заказа нет")
    void getOrderById_whenOrderDoesNotExist_throwsException() {
        // given
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when and then
        assertThatThrownBy(() -> orderService.getOrderById(ORDER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Заказ с id: " + ORDER_ID + " не найден");

        verify(orderRepository).findById(ORDER_ID);
        verify(orderMapper, never()).toResponseDto(any());
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderMapper, customerRepository, productRepository);
    }

    @Test
    @DisplayName("removeOrder: удаляет заказ и возвращает id удаленного заказа")
    void removeOrder_whenOrderExists_removeAndReturnId() {
        // given
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(2L);

        when(orderRepository.findById(2L)).thenReturn(Optional.of(orderEntity));

        // when
        Long id = orderService.removeOrder(2L);

        // then
        assertThat(id).isEqualTo(2L);

        verify(orderRepository).findById(2L);
        verify(orderRepository).delete(orderEntity);
        verify(orderRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderMapper,  customerRepository, productRepository);
    }

    @Test
    @DisplayName("removeOrder: должен выбросить исключение, если заказа нет")
    void removeOrder_whenOrderDoesNotExist_throwsException() {
        // given
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when and then
        assertThatThrownBy(() -> orderService.removeOrder(ORDER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Заказ с id: " + ORDER_ID + " не найден");

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).delete(any(OrderEntity.class));
        verify(orderRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderMapper, customerRepository, productRepository);
    }

    @Test
    @DisplayName("removeProductInOrder: удаляет товар и возвращает актуальный список товаров заказа")
    void removeProductInOrder_whenProductInOrderExists_deleteAndReturnResponse() {
        // given
        ProductEntity otherEntity = new ProductEntity();
        otherEntity.setId(222L);
        orderEntity.getProducts().addAll(List.of(productEntity, otherEntity));

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(222L);
        when(orderMapper.toProductResponseDtoList(List.of(otherEntity))).thenReturn(List.of(dto));

        // when
       List<ProductResponseDto> result = orderService.removeProductInOrder(ORDER_ID, PRODUCT_ID);

        // then
        verify(orderRepository).findById(ORDER_ID);
        verify(productRepository).findById(PRODUCT_ID);

        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(captor.capture());
        OrderEntity passedToSave = captor.getValue();

        assertThat(passedToSave.getId()).isEqualTo(ORDER_ID);
        assertThat(passedToSave.getProducts())
                .extracting(ProductEntity::getId)
                        .containsExactly(222L);

        assertThat(orderEntity.getProducts())
                .extracting(ProductEntity::getId)
                .containsExactly(222L);

        assertThat(result)
                .extracting(ProductResponseDto::getId)
                .containsExactly(222L);


        verify(orderMapper).toProductResponseDtoList(List.of(otherEntity));
        verify(orderRepository, never()).deleteById(anyLong());
        verify(productRepository, never()).save(any(ProductEntity.class));
        verify(productRepository, never()).delete(any(ProductEntity.class));
        verifyNoMoreInteractions(orderRepository, productRepository, orderMapper);
        verifyNoInteractions(customerRepository);
    }

    @Test
    @DisplayName("removeProductInOrder: должен выбросить исключение, если заказ не найден")
    void removeProductInOrder_whenOrderNotFound_throwsException() {
        // given
        Long notExistedOrderId = 12L;
        when(orderRepository.findById(notExistedOrderId)).thenReturn(Optional.empty());

        // when and then
        assertThatThrownBy(() -> orderService.removeProductInOrder(notExistedOrderId, PRODUCT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Заказ с id: 12 не найден");

        verify(orderRepository).findById(notExistedOrderId);
        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(orderRepository, never()).deleteById(anyLong());
        verify(orderMapper, never()).toResponseDto(any(OrderEntity.class));
        verifyNoMoreInteractions(customerRepository, orderRepository, orderMapper, productRepository);
    }

    @Test
    @DisplayName("removeProductInOrder: должен выбросить исключение, если товар не найден")
    void removeProductInOrder_whenProductNotFound_throwsException() {
        // given
        Long notExistedProductId = 11L;
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));
        when(productRepository.findById(notExistedProductId)).thenReturn(Optional.empty());

        // when and then
        List<Long> before = orderEntity.getProducts().stream().map(ProductEntity::getId).toList();

        assertThatThrownBy(() -> orderService.removeProductInOrder(ORDER_ID, notExistedProductId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Товар с id: 11 не найден");

        List<Long> after = orderEntity.getProducts().stream().map(ProductEntity::getId).toList();

        assertThat(after).containsExactlyElementsOf(before);

        verify(orderRepository).findById(ORDER_ID);
        verify(productRepository).findById(notExistedProductId);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(orderRepository, never()).deleteById(anyLong());
        verify(orderMapper, never()).toResponseDto(any(OrderEntity.class));
        verifyNoMoreInteractions(customerRepository, orderRepository, orderMapper, productRepository);
    }

    @Test
    @DisplayName("updateOrder: обновляет заказ, если статус изменился и маппится сохраненная сущность")
    void updateOrder_whenStatusChanged_updateAndReturnResponse() {
        // given
        orderEntity.setOrderStatus(OrderStatus.NEW);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));

        OrderEntity savedEntity = new OrderEntity();
        savedEntity.setId(ORDER_ID);
        savedEntity.setOrderStatus(OrderStatus.PROCESSING);

        OrderResponseDto dtoSaved = new OrderResponseDto();
        dtoSaved.setId(ORDER_ID);
        dtoSaved.setOrderStatus(OrderStatus.PROCESSING);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);
        when(orderMapper.toResponseDto(savedEntity)).thenReturn(dtoSaved);

        // when
        OrderResponseDto result = orderService.updateOrder(ORDER_ID, requestDto(OrderStatus.PROCESSING));

        // then
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(orderEntity.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderMapper).toResponseDto(savedEntity);
        verifyNoMoreInteractions(orderRepository, orderMapper);
        verifyNoInteractions(productRepository, customerRepository);
    }

    @Test
    @DisplayName("updateOrder: бросает исключение, если заказ не найден")
    void updateOrder_whenOrderNotFound_throwsException() {
        // given
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // when and then
        assertThatThrownBy(() -> orderService.updateOrder(ORDER_ID, requestDto(OrderStatus.PROCESSING)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Заказ с id: " + ORDER_ID + " не найден");

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(orderMapper, never()).toResponseDto(any(OrderEntity.class));
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderMapper, productRepository, customerRepository);
    }

    @Test
    @DisplayName("updateOrder: не обновляет заказ, если статус заказа = null, и маппится исходный заказ")
    void updateOrder_whenStatusIsNull_noSave() {
        // given
        orderEntity.setOrderStatus(OrderStatus.NEW);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));

        OrderResponseDto currentEntity = new OrderResponseDto();
        currentEntity.setId(ORDER_ID);
        currentEntity.setOrderStatus(OrderStatus.NEW);
        when(orderMapper.toResponseDto(orderEntity)).thenReturn(currentEntity);

        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setOrderStatus(null);

        // when
        OrderResponseDto result = orderService.updateOrder(ORDER_ID, requestDto);

        // then
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(orderEntity.getOrderStatus()).isEqualTo(OrderStatus.NEW);
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(orderMapper).toResponseDto(orderEntity);
        verifyNoMoreInteractions(orderRepository, orderMapper);
        verifyNoInteractions(productRepository, customerRepository);
    }

    @Test
    @DisplayName("updateOrder: не обновляет заказ, если статус не изменился, и маппится исходный заказ")
    void updateOrder_whenStatusIsSame_notSave() {
        // given
        orderEntity.setOrderStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(orderEntity));

        OrderResponseDto currentEntity = new OrderResponseDto();
        currentEntity.setId(ORDER_ID);
        currentEntity.setOrderStatus(OrderStatus.PROCESSING);

        when(orderMapper.toResponseDto(orderEntity)).thenReturn(currentEntity);

        // when
        OrderResponseDto result = orderService.updateOrder(ORDER_ID, requestDto(OrderStatus.PROCESSING));

        // then
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(orderEntity.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(orderMapper).toResponseDto(orderEntity);
        verifyNoMoreInteractions(orderRepository, orderMapper);
        verifyNoInteractions(productRepository, customerRepository);
    }
}