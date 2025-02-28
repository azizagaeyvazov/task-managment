package az.taskmanagementsystem.mapper;

import az.taskmanagementsystem.dto.ProfileUpdateRequest;
import az.taskmanagementsystem.dto.RegisterRequest;
import az.taskmanagementsystem.dto.UserResponse;
import az.taskmanagementsystem.dto.UserUpdateRequest;
import az.taskmanagementsystem.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User dtoToEntity(RegisterRequest request);

    UserResponse entityToDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserProfile(ProfileUpdateRequest request, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserUpdateRequest request, @MappingTarget User user);

    @Mapping(target = "password", ignore = true)
    void updateUserRegister(RegisterRequest request, @MappingTarget User user);


}
