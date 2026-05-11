package com.bank.auth_service.mapper;

import com.bank.auth_service.dto.request.RegisterRequest;

import com.bank.auth_service.entity.User;
import com.bank.bank_common.dto.user.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "phone", target = "phone") // 👈 ép map rõ ràng
    UserResponse toResponse(User user);

    User toEntity(RegisterRequest registerRequest);
}
