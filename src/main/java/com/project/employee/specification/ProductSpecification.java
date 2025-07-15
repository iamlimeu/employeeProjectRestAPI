package com.project.employee.specification;

import com.project.employee.entity.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ProductSpecification {
    public static Specification<ProductEntity> filter(String name, String description, BigDecimal price) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (name != null) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (description != null) {
                predicates.add(cb.equal(root.get("description"), description));
            }
            if (price != null) {
                predicates.add(cb.equal(root.get("price"), price));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
