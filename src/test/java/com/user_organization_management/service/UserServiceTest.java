package com.user_organization_management.service;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.model.CustomPageResponse;
import com.user_organization_management.repository.OrganizationRepository;
import com.user_organization_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserDTO userDTO;
    private OrganizationEntity organizationEntity;

    @BeforeEach
    void setUp() {
        organizationEntity = new OrganizationEntity();
        organizationEntity.setId(1L);
        organizationEntity.setName("Test Org");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setMobile("1234567890");
        userEntity.setOrganization(organizationEntity);
        userEntity.setPassword("encodedPassword");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setMobile("1234567890");
        userDTO.setOrganizationId(1L);
        userDTO.setPassword("123");
    }


    @Test
    void getUsersByFilterWithPagination_ShouldReturnPageOfUsers(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserEntity> userPage = new PageImpl<>(Arrays.asList(userEntity), pageable, 1);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        CustomPageResponse<UserDTO> result =
                userService.getUsersByFilterWithPagination("test@example.com", "1234567890", 0, 10);
        assertNotNull(userPage);
        assertEquals(1 , result.getContent().size());
        assertEquals(1 , result.getTotalPages());
        assertEquals(0 , result.getCurrentPage());
        assertEquals(1 , result.getTotalElements());
        assertEquals("test@example.com" , result.getContent().get(0).getEmail());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        UserDTO result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("Test User", result.getName());

    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        UserDTO  result = userService.getUserByEmail("test@example.com");
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> {
            userService.getUserByEmail("test@example.com");
        });
        assertEquals("User with email " + "test@example.com" + " not found", exception.getMessage());
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(id);
        });
        assertEquals("User with id " + id + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void createUser_WithOrganization_ShouldReturnCreatedUser(){
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organizationEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        UserDTO result = userService.createUser(userDTO);
        assertNotNull(result);
        assertEquals("Test User" , result.getName());
        assertEquals(1L , result.getOrganizationId());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_WithoutOrganization_ShouldReturnCreatedUser(){
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        userDTO.setOrganizationId(null);
        userEntity.setOrganization(null);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        UserDTO result = userService.createUser(userDTO);
        assertNotNull(result);
        assertNull(result.getOrganizationId());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_WithInvalidOrganization_ShouldThrowException(){
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.createUser(userDTO);
        });

        assertEquals("Organization not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {
        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setName("Updated Name");
        updatedDTO.setEmail("updated@example.com");
        updatedDTO.setMobile("9876543210");
        updatedDTO.setOrganizationId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organizationEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO result = userService.updateUser(1L, updatedDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).save(userEntity);
        assertEquals("Updated Name", userEntity.getName());
        assertEquals("updated@example.com", userEntity.getEmail());
        assertEquals("9876543210", userEntity.getMobile());
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> {
            userService.updateUser(1L , userDTO);
        });
       assertEquals("User not found", exception.getMessage());
       verify(userRepository, times(1)).findById(1L);

    }

    @Test
    void assignUserToOrganization_WhenBothExist_ShouldReturnUpdatedUser(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organizationEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        UserDTO result = userService.assignUserToOrganization(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrganizationId());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void assignUserToOrganization_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> {
            userService.assignUserToOrganization(1L , 1L);
        });
        assertEquals("User not found", exception.getMessage());    }

    @Test
    void assignUserToOrganization_WhenOrgNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> {
            userService.updateUser(1L , userDTO);
        });
        assertEquals("Organization not found", exception.getMessage());
    }

    @Test
    void unassignUserFromOrganization_WhenUserExists_ShouldReturnUpdatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        UserDTO result = userService.unassignUserFromOrganization(1L);

        assertNotNull(result);
        assertNull(userEntity.getOrganization());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void unassignUserFromOrganization_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> {
            userService.unassignUserFromOrganization(1L );
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> {
            userService.deleteUser(1L );
        });
        assertEquals("User with id " + 1 + " not found", exception.getMessage());    }

}