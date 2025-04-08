package com.user_organization_management.mapper;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-08T20:14:16+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setOrganizationId( userOrganizationId( user ) );
        userDTO.setOrganizationName( userOrganizationName( user ) );
        userDTO.setEmail( user.getEmail() );
        userDTO.setId( user.getId() );
        userDTO.setMobile( user.getMobile() );
        userDTO.setName( user.getName() );
        userDTO.setPassword( user.getPassword() );

        return userDTO;
    }

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setOrganization( userDTOToOrganizationEntity( userDTO ) );
        userEntity.setId( userDTO.getId() );
        userEntity.setEmail( userDTO.getEmail() );
        userEntity.setMobile( userDTO.getMobile() );
        userEntity.setName( userDTO.getName() );
        userEntity.setPassword( userDTO.getPassword() );

        return userEntity;
    }

    private Long userOrganizationId(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }
        OrganizationEntity organization = userEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        Long id = organization.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String userOrganizationName(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }
        OrganizationEntity organization = userEntity.getOrganization();
        if ( organization == null ) {
            return null;
        }
        String name = organization.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    protected OrganizationEntity userDTOToOrganizationEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        OrganizationEntity organizationEntity = new OrganizationEntity();

        organizationEntity.setId( userDTO.getOrganizationId() );

        return organizationEntity;
    }
}
