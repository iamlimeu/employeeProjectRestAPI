package com.project.employee.repository;

import com.project.employee.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>,
                                         JpaSpecificationExecutor<OrderEntity> {

    @Query("""
           select distinct o 
           from OrderEntity o 
           left join fetch o.products 
           where o.id = :id
           """)
    Optional<OrderEntity> findByIdWithProducts(@Param("id") Long id);

}
