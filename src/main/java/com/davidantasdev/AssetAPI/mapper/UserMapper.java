package com.davidantasdev.AssetAPI.mapper;

import com.davidantasdev.AssetAPI.dto.request.UserRequest;
import com.davidantasdev.AssetAPI.dto.response.UserResponse;
import com.davidantasdev.AssetAPI.entity.User;
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
