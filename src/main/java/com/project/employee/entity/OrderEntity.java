package com.project.employee.entity;

import com.project.employee.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToMany
    @JoinTable(name = "product_order",
        joinColumns = @JoinColumn(name = "order_id",
            foreignKey = @ForeignKey(foreignKeyDefinition =
            "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT")),
        inverseJoinColumns = @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(foreignKeyDefinition =
            "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT")))
    private List<ProductEntity> products;

    @PreRemove
    private void checkProductsBeforeDelete() {
        if (!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete order with associated products");
        }
    }
}
