package com.project.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.project.employee.dto.OrderRequestDto;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import com.project.employee.entity.ProductEntity;
import com.project.employee.enums.OrderStatus;
import com.project.employee.repository.CustomerRepository;
import com.project.employee.repository.OrderRepository;
import com.project.employee.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderControllerIT {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", pg::getJdbcUrl);
        registry.add("spring.datasource.username", pg::getUsername);
        registry.add("spring.datasource.password", pg::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void clean() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
    }

    private CustomerEntity customer(String firstName, String lastName, String email, String phoneNumber) {
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customerRepository.save(customer);
    }

    private ProductEntity product(String name, String description, BigDecimal price) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return productRepository.save(product);
    }

    @Test
    @DisplayName("POST /orders: 201 и заказ создался в БД")
    void addOrder_returns201_andPersists() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");

        var body = new OrderRequestDto();
        body.setCustomerId(customer.getId());
        body.setOrderStatus(OrderStatus.NEW);

        var mvc = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.orderStatus").value("NEW"))
                .andReturn();

        var id = JsonPath.read(mvc.getResponse().getContentAsString(), "$.id");
        var saved = orderRepository.findById(Long.valueOf(String.valueOf(id))).orElseThrow();

        assertThat(saved.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(saved.getOrderStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    @DisplayName("POST /orders: 400 при отсутствии обязательного поля orderStatus")
    void createOrder_whenMissOrderStatus_validation400() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");

        var body = new OrderRequestDto();
        body.setCustomerId(customer.getId());
        body.setOrderStatus(null);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /orders/{id}/products/{pid}: 202 и товар добавлен в заказ")
    void addProductToOrder_returns202_andPersists() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");
        var product = product("Jelly bear", "yummy and chewy", BigDecimal.valueOf(100.00));
        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/products/{productId}", order.getId(), product.getId()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(order.getId().longValue()))
                .andExpect(jsonPath("$.productInfo[0].id").value(product.getId().longValue()))
                .andExpect(jsonPath("$.productInfo[0].name").value("Jelly bear"));

        var reloaded = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();
        assertThat(reloaded.getProducts())
                .extracting(ProductEntity::getId)
                .containsExactly(product.getId());
    }

    @Test
    @DisplayName("GET /orders: фильтрует по статусу и отдает пагинацию")
    void getAllOrders_whenFiltersByStatus_returnsPages() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");
        var product1 = product("Jelly bear", "yummy and chewy", BigDecimal.valueOf(100.00));
        var product2 = product("Chocolate cookie",
                "crunchy with rich chocolate flavor", BigDecimal.valueOf(50.00));

        var order1 = new OrderEntity();
        order1.setCustomer(customer);
        order1.setOrderStatus(OrderStatus.NEW);
        order1.setProducts(new ArrayList<>(List.of(product1)));
        orderRepository.save(order1);

        var order2 = new OrderEntity();
        order2.setCustomer(customer);
        order2.setOrderStatus(OrderStatus.NEW);
        order2.setProducts(new ArrayList<>(List.of(product2)));
        orderRepository.save(order2);

        mockMvc.perform(get("/orders")
                .param("orderStatus", "NEW")
                .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("GET /orders/{id}: 200, если найден")
    void getOrderById_whenFound_returns200() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");
        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order);

        mockMvc.perform(get("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.orderStatus").value("NEW"));
    }

    @Test
    @DisplayName("GET /orders/{id}: 404, если не найден")
    void getOrderById_whenNotFound_returns404() throws Exception {
        mockMvc.perform(get("/orders/{id}", 22222))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /orders/{id}: 200 и заказ удален из БД")
    void removeOrder_whenFound_returns200() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");
        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order);

        mockMvc.perform(delete("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(order.getId().toString()));

        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /orders/{oid}/products/{pid}: 200 и в ответе останутся только оставшиеся товары")
    void removeProductFromOrder_whenFound_returns200() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");
        var product1 = product("Jelly bear", "yummy and chewy", BigDecimal.valueOf(100.00));
        var product2 = product("Chocolate cookie",
                "crunchy with rich chocolate flavor", BigDecimal.valueOf(50.00));

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        order.setProducts(new ArrayList<>(List.of(product1, product2)));
        orderRepository.save(order);

        mockMvc.perform(delete("/orders/{orderId}/products/{productId}",  order.getId(), product1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(product2.getId().longValue()))
                .andExpect(jsonPath("$[0].name").value("Chocolate cookie"));

        var reloaded = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();
        assertThat(reloaded.getProducts())
                .extracting(ProductEntity::getId)
                .containsExactly(product2.getId());
    }

    @Test
    @DisplayName("PATCH /orders/{id}: 200 и статус обновлен в БД")
    void updateOrder_when200_returnsUpdatedStatus() throws Exception {
        var customer = customer("Evgeny", "Lim", "test@gmail.com", "+79281112233");
        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order);

        var body = new OrderRequestDto();
        body.setOrderStatus(OrderStatus.PROCESSING);

        mockMvc.perform(patch("/orders/{id}", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PROCESSING"));

        var reloaded = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
    }
}