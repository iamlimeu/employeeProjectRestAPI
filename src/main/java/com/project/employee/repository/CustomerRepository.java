package com.project.employee.repository;

import com.project.employee.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>,
                                            JpaSpecificationExecutor<CustomerEntity> {
}
