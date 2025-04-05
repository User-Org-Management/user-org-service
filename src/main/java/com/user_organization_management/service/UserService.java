package com.user_organization_management.service;

import java.util.List;
import java.util.Optional;

import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.mapper.UserMapper;
import com.user_organization_management.model.CustomPageResponse;
import com.user_organization_management.specification.UserSpecification;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.repository.UserRepository;
import static com.user_organization_management.util.Constants.*;


@Service
public class UserService {
	
    private static final Logger logger = LoggerFactory.getLogger(UserService.class); 

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired 
	private OrganizationService organizationService;

	@Autowired
	private OrganizationMapper organizationMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final UserMapper userMapper = UserMapper.INSTANCE;


	public CustomPageResponse<UserDTO> getUsersByFilterWithPagination(String email, String mobile, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		Specification<UserEntity> spec = UserSpecification.filterByEmailAndMobile(email, mobile);
		Page<UserEntity> userPage = userRepository.findAll(spec, pageable);
		List<UserDTO> users = userPage.getContent().stream().map(userMapper::toDTO).toList();
		return CustomPageResponse.<UserDTO>builder()
				.content(users)
				.currentPage(userPage.getNumber())
				.totalPages(userPage.getTotalPages())
				.totalElements(userPage.getTotalElements())
				.isFirst(userPage.isFirst())
				.isLast(userPage.isLast())
				.pageSize(userPage.getSize())
				.build();
	}

	public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
	}

	@Transactional
	public UserDTO createUser(UserDTO userDto) {
		logger.info("create a new user with email: {}", userDto.getEmail());
		checkUserEmailExist(userDto.getEmail(), userDto.getId());
		UserEntity user = userMapper.toEntity(userDto);

		if (userDto.getOrganizationId() != null) {
			OrganizationEntity organization = getOrganizationById(userDto.getOrganizationId());
			user.setOrganization(organization);
			logger.info("User {} is being assigned to organization with ID: {}", userDto.getEmail(), userDto.getOrganizationId());
		} else {
			user.setOrganization(null);
			logger.info("User {} is not assigned to any organization.", userDto.getEmail());
		}
		if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
			logger.error("Password cannot be empty for user {}", userDto.getEmail());
			throw new IllegalArgumentException(PASSWORD_CAN_NOT_BE_EMPTY);
		}
		String encodedPassword = passwordEncoder.encode(userDto.getPassword());
		user.setPassword(encodedPassword);
		logger.info("Password for user {} has been encoded successfully.", userDto.getEmail());

		UserDTO savedUserDto = userMapper.toDTO(userRepository.save(user));
		logger.info("User {} has been successfully created with ID: {}", savedUserDto.getEmail(), savedUserDto.getId());

		return savedUserDto;
	}

	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		UserEntity existingUser = userMapper.toEntity(getUserById(userId));
		checkUserEmailExist(userDTO.getEmail(), userId);
		existingUser.setName(userDTO.getName());
		existingUser.setEmail(userDTO.getEmail());
		existingUser.setMobile(userDTO.getMobile());
		if (userDTO.getOrganizationId() != null) {
			OrganizationEntity organization = getOrganizationById(userDTO.getOrganizationId());
			existingUser.setOrganization(organization);
		}
		UserEntity updatedUser = userRepository.save(existingUser);
		return userMapper.toDTO(updatedUser);
	}



	public UserDTO assignUserToOrganization(Long userId, Long orgId) {
		UserEntity user = userMapper.toEntity(getUserById(userId));
		OrganizationEntity organization = getOrganizationById(orgId);
		user.setOrganization(organization);
		return userMapper.toDTO(userRepository.save(user));
	}
	
	public UserDTO unassignUserFromOrganization(Long userId) {
		logger.info("un assign user: {}", userId);
		UserEntity user = userMapper.toEntity(getUserById(userId));
	    user.setOrganization(null);
		logger.info("user after un assign : {}", user);
		return userMapper.toDTO(userRepository.save(user));
	}
	
	public void deleteUser(Long id) {
		logger.info("check User with id :{}", id);
		UserEntity user = userMapper.toEntity(getUserById(id));
		logger.info("user  deleted: {}", user);
	    userRepository.deleteById(user.getId());
	}

	public UserDTO getUserByEmail(String email){
		return userRepository.
				findByEmail(email).map(userMapper::toDTO)
				.orElseThrow(() -> new  EntityNotFoundException(String.format(USER_WITH_EMAIL_NOT_FOUND, email)));
	}


	public void checkUserEmailExist(String email, Long userId){
		UserEntity existingUser = userMapper.toEntity(getUserByEmail(email));
		if (!existingUser.getId().equals(userId)) {
			logger.warn("Email {} already exists.", email);
			throw new IllegalArgumentException(USER_EMAIL_EXISTS);
		}
	}
	public OrganizationEntity getOrganizationById(Long orgId){
        return organizationMapper.toEntity(organizationService.getOrganizationById(orgId));
	}
}
