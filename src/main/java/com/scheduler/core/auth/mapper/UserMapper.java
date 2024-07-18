package com.scheduler.core.auth.mapper;

import com.scheduler.core.auth.dto.NewUserCreatedDTO;
import com.scheduler.core.auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

    @Mapping(target = "username", source = "user.name")
    NewUserCreatedDTO toUserCreatedDTO(User user);

}