package com.project.employee.repository;

import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import com.project.employee.entity.ProductEntity;
import com.project.employee.enums.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryIT {

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
        registry.add("spring.jpa.open-in-view", () -> "false");
    }

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void clean() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    private CustomerEntity saveCustomer(String firstName, String lastName, String email, String phoneNumber) {
        var customer = new CustomerEntity();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customerRepository.save(customer);
    }

    private ProductEntity saveProduct(String name, String description, BigDecimal price) {
        var product = new ProductEntity();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return productRepository.save(product);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findByIdWithProducts: загружает заказ с продуктами(используя fetch join")
    void findByIdWithProducts_fetchesProducts() {
        var customer = saveCustomer("Evgeny", "Lim",
                "test@gmail.com", "+79281223344");
        var product1 = saveProduct("Jelly bear", "yummy and chewy", BigDecimal.valueOf(100));
        var product2 = saveProduct("Chocolate cookie", "crunchy", BigDecimal.valueOf(50));

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        order.setProducts(new ArrayList<>(List.of(product1, product2)));
        orderRepository.save(order);

        flushAndClear();

        var loaded = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();

        assertThat(loaded.getId()).isEqualTo(order.getId());
        assertThat(Hibernate.isInitialized(loaded.getProducts())).isTrue();
        assertThat(loaded.getProducts())
                .extracting(ProductEntity::getId)
                .containsExactlyInAnyOrder(product1.getId(), product2.getId());
    }

    @Test
    @DisplayName("findByIdWithProducts: возвращает пустую инициализированную коллекцию, если товаров нет")
    void findByIdWithProducts_whenNoProducts_returnsEmptyList() {
        var customer = saveCustomer("Evgeny", "Lim",
                "test@gmail.com", "+79281223344");
        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        order.setProducts(new ArrayList<>());
        orderRepository.save(order);

        flushAndClear();

        var loaded = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();

        assertThat(loaded.getProducts()).isEmpty();
        assertThat(Hibernate.isInitialized(loaded.getProducts())).isTrue();
    }

    @Test
    @DisplayName("save: сохраняет связь many-to-many в product_order")
    void save_persistsJoinTable() {
        var customer = saveCustomer("Evgeny", "Lim",
                "test@gmail.com", "+79281223344");
        var product1 = saveProduct("Jelly bear", "yummy and chewy", BigDecimal.valueOf(100));
        var product2 = saveProduct("Chocolate cookie", "crunchy", BigDecimal.valueOf(50));

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        order.setProducts(new ArrayList<>(List.of(product1, product2)));
        orderRepository.save(order);

        flushAndClear();

        var reloaded = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();
        assertThat(reloaded.getProducts()).hasSize(2);
    }

    @Test
    @DisplayName("Удаление товара из коллекции и save: запись удалиться из join-таблицы")
    void removeProduct_whenExists_updatesJoinTable() {
        var customer = saveCustomer("Evgeny", "Lim",
                "test@gmail.com", "+79281223344");
        var product1 = saveProduct("Jelly bear", "yummy and chewy", BigDecimal.valueOf(100));
        var product2 = saveProduct("Chocolate cookie", "crunchy", BigDecimal.valueOf(50));

        var order = new OrderEntity();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.NEW);
        order.setProducts(new ArrayList<>(List.of(product1, product2)));
        orderRepository.save(order);

        flushAndClear();

        var loaded = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();
        loaded.getProducts().removeIf(p -> p.getId().equals(product1.getId()));
        orderRepository.save(loaded);

        flushAndClear();

        var after = orderRepository.findByIdWithProducts(order.getId()).orElseThrow();
        assertThat(after.getProducts())
                .extracting(ProductEntity::getId)
                .containsExactly(product2.getId());
    }

    @Test
    @DisplayName("findByIdWithProducts: пустой Optional для несуществующего id")
    void findByIdWithProducts_whenNotExists_returnsEmptyOptional() {
        Optional<OrderEntity> missingProduct = orderRepository.findByIdWithProducts(123123L);
        assertThat(missingProduct).isEmpty();
    }
}