package com.project.employee.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PageableAssembler {
    public Pageable from(int page, int size, String[] sortParams) {
        List<Sort.Order> orders = Arrays.stream(sortParams)
                .map(s -> {
                    var parts = s.split(",");
                    return new Sort.Order(
                            Sort.Direction.fromString(parts[1].trim()),
                            parts[0].trim()
                    );
                })
                .collect(Collectors.toList());

        return PageRequest.of(page, size, Sort.by(orders));
    }
}
