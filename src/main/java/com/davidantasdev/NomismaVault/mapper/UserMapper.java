package com.davidantasdev.NomismaVault.mapper;

import com.davidantasdev.NomismaVault.dto.request.UserRequest;
import com.davidantasdev.NomismaVault.dto.response.UserResponse;
import com.davidantasdev.NomismaVault.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toEntity(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);

    List<UserResponse> toResponseList(List<User> users);
}
