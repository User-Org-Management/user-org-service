package com.user_organization_management.mapper;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(UserEntity user);

    @Mapping(source = "organizationId", target = "organization.id")
    @Mapping(target = "organization.name", ignore = true) // Avoid overriding name
    UserEntity toEntity(UserDTO userDTO);
}
