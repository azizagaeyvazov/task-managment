package az.taskmanagementsystem.mapper;

import az.taskmanagementsystem.dto.RegisterRequest;
import az.taskmanagementsystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User dtoToEntity(RegisterRequest request);

    @Mapping(target = "password", ignore = true)
    void updateUserEntity(RegisterRequest request, @MappingTarget User user);

}
