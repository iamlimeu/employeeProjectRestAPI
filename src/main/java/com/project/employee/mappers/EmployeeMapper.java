package com.project.employee.mappers;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeEntity toEntity(EmployeeRequestDto dto);

    @Mapping(target = "info", source = "entity",
            qualifiedByName = "getEmployeeInfo")
    EmployeeResponseDto toResponseDto(EmployeeEntity entity);

    @Named("getEmployeeInfo")
    default String getEmployeeInfo(EmployeeEntity entity) {
        return entity.getFirstName() + " " + entity.getLastName() +
                " ,email: " + entity.getEmail() +
                " ,password: " + entity.getPassword() +
                " ,role: " + entity.getRole();
    }
}
