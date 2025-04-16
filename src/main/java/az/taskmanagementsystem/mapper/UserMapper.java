package az.taskmanagementsystem.mapper;

import az.taskmanagementsystem.dto.ProfileUpdateRequest;
import az.taskmanagementsystem.dto.RegisterRequest;
import az.taskmanagementsystem.dto.UserResponse;
import az.taskmanagementsystem.entity.User;
import az.taskmanagementsystem.enums.Role;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = TaskMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User dtoToEntity(RegisterRequest request);

    UserResponse entityToDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void  updateUserProfile(ProfileUpdateRequest request, @MappingTarget User user);

    @Mapping(target = "password", ignore = true)
    void updateUserRegister(RegisterRequest request, @MappingTarget User user);

    default UserResponse mapUserBasedOnRole(User user, Role role) {
        var response = entityToDto(user);
        if (role.equals(Role.MANAGER)) {
            response.setAssignedTasks(null);
        } else if (role.equals(Role.EMPLOYEE)) {
            response.setCreatedTasks(null);
        }
        return response;
    }


}
