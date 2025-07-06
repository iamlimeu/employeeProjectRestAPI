package com.project.employee.specification;

import com.project.employee.entity.EmployeeEntity;
import com.project.employee.enums.EmployeeRole;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

public class EmployeeSpecification {
    public static Specification<EmployeeEntity> filter(
            String firstName,
            String lastName,
            String emailLike,
            EmployeeRole role
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
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
