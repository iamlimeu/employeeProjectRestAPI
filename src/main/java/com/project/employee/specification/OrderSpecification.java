package com.project.employee.specification;

import com.project.employee.entity.OrderEntity;
import com.project.employee.enums.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderSpecification {
    public static Specification<OrderEntity> filter(LocalDateTime createdDate, OrderStatus orderStatus) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (createdDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), createdDate));
            }
            if (orderStatus != null) {
                predicates.add(cb.equal(root.get("orderStatus"), orderStatus));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
