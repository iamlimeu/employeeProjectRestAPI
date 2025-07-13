package com.project.employee.mappers;

import com.project.employee.dto.OrderRequestDto;
import com.project.employee.dto.OrderResponseDto;
import com.project.employee.entity.CustomerEntity;
import com.project.employee.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderEntity toEntity(OrderRequestDto dto);

    @Mapping(target = "info", source = ".",
            qualifiedByName = "getOrderInfo")
    @Mapping(target = "customerInfo", source = "customer",
             qualifiedByName = "mapCustomerInfo")
    OrderResponseDto  toResponseDto(OrderEntity entity);

    @Named("getOrderInfo")
    default String getOrderInfo(OrderEntity entity) {
        Long customerId = entity.getCustomer() != null ? entity.getCustomer().getId() : null;
        return "Created date: " + entity.getCreatedDate() +
                " ,order status: " + entity.getOrderStatus() +
                " , Customer id: " + customerId;
    }

    @Named("mapCustomerInfo")
    default OrderResponseDto.CustomerInfo mapCustomerInfo(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        OrderResponseDto.CustomerInfo info = new OrderResponseDto.CustomerInfo();
        info.setId(entity.getId());
        info.setFirstName(entity.getFirstName());
        info.setLastName(entity.getLastName());
        info.setEmail(entity.getEmail());
        info.setPhoneNumber(entity.getPhoneNumber());
        return info;
    }
}
