package com.project.employee.mappers;

import com.project.employee.dto.ProductRequestDto;
import com.project.employee.dto.ProductResponseDto;
import com.project.employee.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductRequestDto dto);

//    @Mapping(target = "info", source = ".",
//            qualifiedByName = "getProductInfo")
    ProductResponseDto toResponseDto(ProductEntity entity);

//    @Named("getProductInfo")
//    default String getProductInfo(ProductEntity entity) {
//        if (entity == null) {
//            return null;
//        }
//        return "Product name: " + entity.getName() +
//                " | Description: " + entity.getDescription() +
//                " | Price: " + String.format("%.2f RUB", entity.getPrice()
//        );
//    }
}
