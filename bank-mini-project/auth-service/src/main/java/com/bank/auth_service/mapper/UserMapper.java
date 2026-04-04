package com.bank.auth_service.mapper;

import com.bank.auth_service.dto.request.RegisterRequest;
import com.bank.auth_service.dto.response.UserResponse;
import com.bank.auth_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.name", target = "roleName")
    UserResponse toResponse(User user);
    User toEntity(RegisterRequest registerRequest);
}
