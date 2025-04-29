package com.user_organization_management.mapper;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
//    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(UserEntity user);

    @Mapping(source = "organizationId", target = "organization.id")
    @Mapping(target = "organization.name", ignore = true)
    @Mapping(source = "roleId", target = "role.id")
    @Mapping(source = "roleName" ,target = "role.name")
    UserEntity toEntity(UserDTO userDTO);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "failedCount", ignore = true)
    @Mapping(target = "locked", ignore = true)
    UserEntity updateEntityFromDTO(UserDTO userDTO, @MappingTarget UserEntity user);

    @Mapping(target = "failedCount", source = "failedCount")
    @Mapping(target = "locked", source = "locked")
    UserEntity toEntityWithSecurityFields(UserDTO dto);

}
