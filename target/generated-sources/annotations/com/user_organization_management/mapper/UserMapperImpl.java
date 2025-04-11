package com.user_organization_management.mapper;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.RoleEntity;
import com.user_organization_management.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-11T18:17:36+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setOrganizationId( userOrganizationId( user ) );
        userDTO.setOrganizationName( userOrganizationName( user ) );
        userDTO.setRoleId( userRoleId( user ) );
        userDTO.setRoleName( userRoleName( user ) );
        userDTO.setId( user.getId() );
        userDTO.setName( user.getName() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setMobile( user.getMobile() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setFailedCount( user.getFailedCount() );
        userDTO.setLocked( user.isLocked() );

        return userDTO;
    }

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setOrganization( userDTOToOrganizationEntity( userDTO ) );
        userEntity.setRole( userDTOToRoleEntity( userDTO ) );
        userEntity.setId( userDTO.getId() );
        userEntity.setName( userDTO.getName() );
        userEntity.setEmail( userDTO.getEmail() );
        userEntity.setMobile( userDTO.getMobile() );
        userEntity.setPassword( userDTO.getPassword() );
        userEntity.setFailedCount( userDTO.getFailedCount() );
        userEntity.setLocked( userDTO.isLocked() );

        return userEntity;
    }

    @Override
    public UserEntity toEntityWithSecurityFields(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setFailedCount( dto.getFailedCount() );
        userEntity.setLocked( dto.isLocked() );
        userEntity.setId( dto.getId() );
        userEntity.setName( dto.getName() );
        userEntity.setEmail( dto.getEmail() );
        userEntity.setMobile( dto.getMobile() );
        userEntity.setPassword( dto.getPassword() );

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

    private Long userRoleId(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }
        RoleEntity role = userEntity.getRole();
        if ( role == null ) {
            return null;
        }
        Long id = role.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String userRoleName(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }
        RoleEntity role = userEntity.getRole();
        if ( role == null ) {
            return null;
        }
        String name = role.getName();
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

    protected RoleEntity userDTOToRoleEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setId( userDTO.getRoleId() );
        roleEntity.setName( userDTO.getRoleName() );

        return roleEntity;
    }
}
