package com.project.employee.mappers;

import com.project.employee.dto.CustomerRequestDto;
import com.project.employee.dto.CustomerResponseDto;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface CustomerMapper {

    CustomerEntity toEntity(CustomerRequestDto dto);

//    @Mapping(target = "info", source = ".",
//             qualifiedByName = "getCustomerInfo")
    @Mapping(target = "orders", source = "orders")
    CustomerResponseDto toResponseDto(CustomerEntity entity);

//    @Named("getCustomerInfo")
//    default String getCustomerInfo(CustomerEntity entity) {
//        return entity.getFirstName() + " " + entity.getLastName() +
//                " ,email: " + entity.getEmail() +
//                " ,phone number: " + entity.getPhoneNumber() +
//                " ,orders: " +  (entity.getOrders() == null ? 0 : entity.getOrders().size());
//    }

    @Mapping(source = "orderStatus", target = "status")
    CustomerResponseDto.OrderInfo mapOrderInfo(OrderEntity orderEntity);
}
