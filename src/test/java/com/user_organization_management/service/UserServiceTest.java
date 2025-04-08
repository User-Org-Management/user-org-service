package com.user_organization_management.service;

import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.model.CustomPageResponse;
import com.user_organization_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private OrganizationEntity organizationEntity;
    private OrganizationDTO organizationDTO;
    private UserDTO userDTO = new UserDTO();

    @BeforeEach
    void setUp() {
        organizationDTO = new OrganizationDTO();
        organizationDTO.setId(1L);
        organizationDTO.setName("Test Org");

        organizationEntity = new OrganizationEntity();
        organizationEntity.setId(1L);
        organizationEntity.setName("Test Org");

        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setMobile("1234567890");
        userDTO.setPassword("password");
        userDTO.setOrganizationId(1L);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setMobile("1234567890");
        userEntity.setPassword("encodedPassword");
        userEntity.setOrganization(organizationEntity);
    }

    @Test
    void getUsersByFilterWithPagination_ShouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity), pageable, 1);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        CustomPageResponse<UserDTO> result = userService.getUsersByFilterWithPagination("test@example.com", "1234567890", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test User", result.getContent().get(0).getName());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_ShouldCreateAndReturnUserDTO() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(organizationService.getOrganizationById(1L)).thenReturn(organizationDTO);
        when(organizationMapper.toEntity(any(OrganizationDTO.class))).thenReturn(organizationEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1L, result.getOrganizationId());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        userDTO.setId(null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDTO));
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUserDTO() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Updated User");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setMobile("9876543210");
        updateDTO.setOrganizationId(1L);
        updateDTO.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(organizationService.getOrganizationById(1L)).thenReturn(organizationDTO);
        when(organizationMapper.toEntity(any())).thenReturn(organizationEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any())).thenReturn(userEntity);

        UserDTO result = userService.updateUser(1L, updateDTO);

        verify(userRepository, times(2)).findById(1L);
        assertNotNull(result);
        assertEquals("updated@example.com", userEntity.getEmail());
        assertEquals("Updated User", userEntity.getName());
        assertEquals("9876543210", userEntity.getMobile());
        assertEquals("newEncodedPassword", userEntity.getPassword());
        assertEquals(organizationEntity, userEntity.getOrganization());

        verify(userRepository).findByEmail("updated@example.com");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(userEntity);
    }

    @Test
    void assignUserToOrganization_ShouldAssignAndReturnUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(organizationService.getOrganizationById(1L)).thenReturn(organizationDTO);
        when(organizationMapper.toEntity(any(OrganizationDTO.class))).thenReturn(organizationEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO result = userService.assignUserToOrganization(1L, 1L);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(captor.capture());
        assertEquals(organizationEntity, captor.getValue().getOrganization());
        assertNotNull(result);
        assertEquals(1L, captor.getValue().getOrganization().getId());
        verify(organizationService).getOrganizationById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void unassignUserFromOrganization_ShouldUnassignAndReturnUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO result = userService.unassignUserFromOrganization(1L);

        assertNotNull(result);
        assertNull(userEntity.getOrganization());
        verify(userRepository).findById(1L);
        verify(userRepository).save(userEntity);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUserDTO() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        UserDTO result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail("nonexistent@example.com"));

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void checkUserEmailExist_WhenEmailExistsForDifferentUser_ShouldThrowException() {
        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(IllegalArgumentException.class,
                () -> userService.checkUserEmailExist("test@example.com", 1L));

        verify(userRepository).findByEmail("test@example.com");
    }


    @Test
    void addOrganizationIfNotNull_WithOrgId_ShouldSetOrganization() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(organizationService.getOrganizationById(1L)).thenReturn(organizationDTO);
        when(organizationMapper.toEntity(any(OrganizationDTO.class))).thenReturn(organizationEntity);

        UserEntity result = userService.addOrganizationIfNotNull(1L, 1L);

        assertNotNull(result);
        assertEquals(organizationEntity, result.getOrganization());
        verify(userRepository).findById(1L);
        verify(organizationService).getOrganizationById(1L);
    }

    @Test
    void addOrganizationIfNotNull_WithoutOrgId_ShouldSetOrganizationToNull() {
        userEntity.setOrganization(organizationEntity);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.addOrganizationIfNotNull(1L, null);

        assertNotNull(result);
        assertNull(result.getOrganization());

        verify(userRepository).findById(1L);
        verify(organizationService, never()).getOrganizationById(any());
    }
}