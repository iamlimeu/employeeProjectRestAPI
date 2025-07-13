package com.project.employee.specification;

import com.project.employee.entity.CustomerEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class CustomerSpecification {
    public static Specification<CustomerEntity> filter(
            String firstName,
            String lastName,
            String emailLike,
            String phoneNumber
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (firstName != null) {
                predicates.add(cb.equal(root.get("firstName"), firstName));
            }
            if (lastName != null) {
                predicates.add(cb.equal(root.get("lastName"), lastName));
            }
            if (emailLike != null) {
                predicates.add(cb.like(root.get("email"), "%" + emailLike + "%"));
            }
            if (phoneNumber != null) {
                predicates.add(cb.equal(root.get("phoneNumber"), phoneNumber));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
