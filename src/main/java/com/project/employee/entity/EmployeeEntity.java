package com.project.employee.entity;

import com.project.employee.enums.EmployeeRole;
import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private EmployeeRole role;
}
