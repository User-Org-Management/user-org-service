package com.user_organization_management.service;

import java.util.List;
import java.util.Optional;

import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.repository.OrganizationRepository;
import com.user_organization_management.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class UserService {
	
    private static final Logger logger = LoggerFactory.getLogger(UserService.class); 

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired 
	private OrganizationRepository organizationRepository;

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

	public Optional<UserDTO> getUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found")));
	}

	@Transactional
	public UserDTO createUser(UserDTO userDto) {
		logger.info("create a new user with email: {}", userDto.getEmail());
		checkUserEmailExist(userDto.getEmail(), userDto.getId());
		UserEntity user = userMapper.toEntity(userDto);

		if (userDto.getOrganizationId() != null) {
			OrganizationEntity organization = organizationRepository.findById(userDto.getOrganizationId())
					.orElseThrow(() -> {
						String errorMsg = "Organization not found with ID: " + userDto.getOrganizationId();
						logger.error(errorMsg);
						return new EntityNotFoundException(errorMsg);
					});
			user.setOrganization(organization);
			logger.info("User {} is being assigned to organization with ID: {}", userDto.getEmail(), userDto.getOrganizationId());
		} else {
			user.setOrganization(null);
			logger.info("User {} is not assigned to any organization.", userDto.getEmail());
		}
		if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
			logger.error("Password cannot be empty for user {}", userDto.getEmail());
			throw new IllegalArgumentException("Password cannot be empty");
		}
		String encodedPassword = passwordEncoder.encode(userDto.getPassword());
		user.setPassword(encodedPassword);
		logger.info("Password for user {} has been encoded successfully.", userDto.getEmail());

		UserDTO savedUserDto = userMapper.toDTO(userRepository.save(user));
		logger.info("User {} has been successfully created with ID: {}", savedUserDto.getEmail(), savedUserDto.getId());

		return savedUserDto;
	}

	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		UserEntity existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
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
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found")) ;
		OrganizationEntity organization = getOrganizationById(orgId);
		user.setOrganization(organization);
		return userMapper.toDTO(userRepository.save(user));
	}
	
	public UserDTO unassignUserFromOrganization(Long userId) {
		logger.info("un assign user: {}", userId);
		UserEntity user = userRepository.findById(userId)
	            .orElseThrow(() -> new EntityNotFoundException("User not found"));
	    user.setOrganization(null);
		logger.info("user after un assign : {}", user);

		return userMapper.toDTO(userRepository.save(user));
	}
	
	public void deleteUser(Long id) {
		logger.info("check User with id :{}", id);
		UserEntity user = userRepository.findById(id).orElseThrow(() ->
		new EntityNotFoundException("User with id " + id + " not found"));	
		
		logger.info("user  deleted: {}", user);
	    userRepository.deleteById(user.getId());
	    //(search) not delete direct
	}

	public Optional<UserDTO> getUserByEmail(String email){
		return Optional.of(userRepository.
				findByEmail(email).map(userMapper::toDTO)
				.orElseThrow(() -> new  EntityNotFoundException("User with email " + email + " not found")));
	}


	public void checkUserEmailExist(String email, Long userId){
		Optional<UserEntity> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
			logger.warn("Email {} already exists.", email);
			throw new IllegalArgumentException("Email already exists!");
		}
	}
	public OrganizationEntity getOrganizationById(Long orgId){
        return organizationRepository.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));
	}
}
