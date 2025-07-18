package com.project.employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @ManyToMany(mappedBy = "products")
    private List<OrderEntity> orders = new ArrayList<>();

    @PreRemove
    private void checkOrdersBeforeDelete() {
        if (!Hibernate.isInitialized(orders)) {
            Hibernate.initialize(orders);
        }
        if (!orders.isEmpty()) {
            throw new IllegalStateException("Cannot delete product referenced in order");
        }
    }
}
