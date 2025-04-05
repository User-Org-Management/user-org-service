package com.user_organization_management.mapper;

import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.entity.OrganizationEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-05T23:44:32+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public OrganizationDTO toDTO(OrganizationEntity organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationDTO organizationDTO = new OrganizationDTO();

        organizationDTO.setId( organization.getId() );
        organizationDTO.setName( organization.getName() );

        return organizationDTO;
    }

    @Override
    public OrganizationEntity toEntity(OrganizationDTO organizationDTO) {
        if ( organizationDTO == null ) {
            return null;
        }

        OrganizationEntity organizationEntity = new OrganizationEntity();

        organizationEntity.setId( organizationDTO.getId() );
        organizationEntity.setName( organizationDTO.getName() );

        return organizationEntity;
    }
}
