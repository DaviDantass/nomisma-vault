package com.davidantasdev.nomismavault.mapper;

import com.davidantasdev.nomismavault.dto.request.UserRequest;
import com.davidantasdev.nomismavault.dto.response.UserResponse;
import com.davidantasdev.nomismavault.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "portfolios", ignore = true)
    @Mapping(target = "priceAlerts", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "portfolios", ignore = true)
    @Mapping(target = "priceAlerts", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);

    List<UserResponse> toResponseList(List<User> users);
}
