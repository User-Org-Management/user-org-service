package com.user_organization_management.mapper;

import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.entity.OrganizationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-08T20:14:15+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
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
