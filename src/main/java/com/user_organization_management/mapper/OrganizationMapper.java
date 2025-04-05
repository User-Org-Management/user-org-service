package com.user_organization_management.mapper;

import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.entity.OrganizationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrganizationMapper {
    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);
    OrganizationDTO toDTO(OrganizationEntity organization);
    OrganizationEntity toEntity(OrganizationDTO organizationDTO);
}
