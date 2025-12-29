package com.davidantasdev.AssetAPI.mapper;

import com.davidantasdev.AssetAPI.dto.request.UserRequest;
import com.davidantasdev.AssetAPI.dto.response.UserResponse;
import com.davidantasdev.AssetAPI.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toEntity(UserRequest request);

    List<UserResponse> toResponseList(List<User> users);
}
