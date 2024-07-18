package auth.mapper;

import auth.dto.NewUserCreatedDTO;
import auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {

    @Mapping(target = "username", source = "user.name")
    NewUserCreatedDTO toUserCreatedDTO(User user);

}