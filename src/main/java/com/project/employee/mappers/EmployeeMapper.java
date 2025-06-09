package com.project.employee.mappers;

import com.project.employee.dto.EmployeeRequestDto;
import com.project.employee.dto.EmployeeResponseDto;
import com.project.employee.entity.EmployeeEntity;

public class EmployeeMapper {
    public static EmployeeResponseDto mapToResponseDto(EmployeeEntity employeeEntity) {
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setId(employeeEntity.getId());
        responseDto.setInfo(employeeEntity.getFirstName() + " " + employeeEntity.getLastName() + " ,email: " +
                employeeEntity.getEmail() + " ,password: " + employeeEntity.getPassword() +
                " ,role: " + employeeEntity.getRole());
        return responseDto;
    }

    public static EmployeeEntity mapToEntity(EmployeeRequestDto employeeRequestDto) {
        EmployeeEntity newEntity = new EmployeeEntity();
        newEntity.setFirstName(employeeRequestDto.getFirstName());
        newEntity.setLastName(employeeRequestDto.getLastName());
        newEntity.setEmail(employeeRequestDto.getEmail());
        newEntity.setPassword(employeeRequestDto.getPassword());
        newEntity.setRole(employeeRequestDto.getRole());
        return newEntity;
    }
}
