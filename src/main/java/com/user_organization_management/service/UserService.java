package com.user_organization_management.service;

import java.util.List;
import java.util.Optional;

import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.RoleEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.AccountLockedException;
import com.user_organization_management.exception.BadCredentialsAuthException;
import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.mapper.UserMapper;
import com.user_organization_management.model.CustomPageResponse;
import com.user_organization_management.repository.RoleRepository;
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
import static com.user_organization_management.utils.Constants.*;

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

	@Autowired
	private RoleRepository roleRepository;

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
	public UserDTO createUser(UserDTO userDTO) {
		logger.info(USER_CREATION, userDTO.getEmail());
		checkUserEmailExist(userDTO.getEmail(), userDTO.getId());
		checkUserNameExist(userDTO, userDTO.getId());
		UserEntity user = userMapper.toEntity(userDTO);
		if (userDTO.getOrganizationId() != null) {
			OrganizationEntity organization = getOrganizationById(userDTO.getOrganizationId());

			user.setOrganization(organization);

			logger.info(USER_ASSIGNED_TO_ORG, userDTO.getEmail(), userDTO.getOrganizationId());
		} else {
			user.setOrganization(null);
			logger.info(USER_NOT_ASSIGNED_TO_ORG, userDTO.getEmail());
		}
		if (userDTO.getRoleId() != null) {
			RoleEntity role = roleRepository.findById(userDTO.getRoleId())
					.orElseThrow(() -> new EntityNotFoundException("Role not found"));
			user.setRole(role);
		}

		String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
		user.setPassword(encodedPassword);
		logger.info(PASSWORD_ENCODED, userDTO.getEmail());

		UserDTO savedUserDto = userMapper.toDTO(userRepository.save(user));
		logger.info(USER_CREATED, savedUserDto.getEmail(), savedUserDto.getId());
		return savedUserDto;
	}

	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		logger.info(USER_UPDATING, userId);
		UserEntity existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

		checkUserEmailExist(userDTO.getEmail(), userId);
		checkUserNameExist(userDTO, userId);

		existingUser.setName(userDTO.getName());
		existingUser.setEmail(userDTO.getEmail());
		existingUser.setMobile(userDTO.getMobile());

		if (userDTO.getOrganizationId() != null) {
			OrganizationEntity organization = getOrganizationById(userDTO.getOrganizationId());
			existingUser.setOrganization(organization);
			logger.info(USER_ASSIGNED_TO_ORG_UPDATE, userDTO.getEmail(), userDTO.getOrganizationId());
		} else {
			existingUser.setOrganization(null);
			logger.info(USER_UNASSIGNED_FROM_ORG_UPDATE, userDTO.getEmail());
		}

		if (userDTO.getRoleId() != null) {
			RoleEntity role = roleRepository.findById(userDTO.getRoleId())
					.orElseThrow(() -> new EntityNotFoundException("Role not found"));
			existingUser.setRole(role);
		}


		if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
			existingUser.setPassword(encodedPassword);
			logger.info(PASSWORD_UPDATED, userDTO.getEmail());
		}

		UserEntity updatedUser = userRepository.save(existingUser);
		logger.info(USER_UPDATED, updatedUser.getEmail());

		return userMapper.toDTO(updatedUser);
	}

	@Transactional
	public UserDTO assignUserToOrganization(Long userId, Long orgId) {
		UserEntity user = userMapper.toEntity(getUserById(userId));
		OrganizationEntity org = getOrganizationById(orgId);
		user.setOrganization(org);
		logger.info(USER_ASSIGNING_ORG, userId, orgId);
		return userMapper.toDTO(userRepository.save(user));
	}

	public UserDTO unassignUserFromOrganization(Long userId) {
		logger.info(USER_UNASSIGNED, userId);
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND, userId)));
		user.setOrganization(null);
		logger.info(USER_UNASSIGNED_LOG, user);
		return userMapper.toDTO(userRepository.save(user));
	}

	public void deleteUser(Long id) {
		logger.info(USER_DELETION, id);
		String idMs = id.toString();
		UserEntity user = userRepository.findById(id).orElseThrow(() ->
				new EntityNotFoundException(String.format(USER_NOT_FOUND, idMs)));

		logger.info(USER_DELETED, user);
		userRepository.deleteById(user.getId());
	}

	public UserDTO getUserByEmail(String email){
		return userRepository.findByEmail(email)
				.map(userMapper::toDTO)
				.orElseThrow(() -> new EntityNotFoundException(String.format(EMAIL_NOT_FOUND, email)));
	}
	public UserDTO getUserByName(String name){
		return  userRepository.findByName(name)
				.map(userMapper::toDTO)
				.orElseThrow(() -> new BadCredentialsAuthException("username or password invalid"));
	}

	public void checkUserEmailExist(String email, Long userId){
		Optional<UserEntity> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
			logger.warn(EMAIL_EXISTS, email);
			throw new IllegalArgumentException(String.format(EMAIL_EXISTS, email));
		}
	}

	public OrganizationEntity getOrganizationById(Long orgId){
		return organizationMapper.toEntity(organizationService.getOrganizationById(orgId));
	}

	public UserEntity addOrganizationIfNotNull(Long userId, Long orgId){
		UserEntity existingUser = userMapper.toEntity(getUserById(userId));
		if (orgId != null) {
			OrganizationEntity organization = getOrganizationById(orgId);
			existingUser.setOrganization(organization);
		} else {
			existingUser.setOrganization(null);
		}
		return existingUser;
	}

	public void checkUserNameExist(UserDTO body, Long id) {
		if (isNameTaken(body.getName(), id)) {
			logger.warn("User name '{}' already exists.", body.getName());
			throw new IllegalArgumentException(String.format(USER_NAME_EXISTS, body.getName()));
		}
	}

	private boolean isNameTaken(String name, Long id) {
		if (id == null) {
			return userRepository.existsByName(name);
		}
		Optional<UserEntity> existingUser = userRepository.findById(id);
        return existingUser.map(userEntity ->
				!userEntity.getName().equals(name) &&
						userRepository.existsByName(name)).orElseGet(() -> userRepository.existsByName(name));
    }

	@Transactional
	public void increaseFailedLoginAttempts(String userName) {
		UserEntity user = userMapper.toEntityWithSecurityFields(getUserByName(userName));
		long count = user.getFailedCount();
		if (count >= 2) {
			user.setLocked(true);
			user.setFailedCount(count + 1);

		} else {
			user.setFailedCount(count + 1);
		}
		userRepository.save(user);
	}

	public void disableLogin(String name){
		UserEntity user = userMapper.toEntityWithSecurityFields(getUserByName(name));
		if (user.isLocked() && user.getFailedCount() >= 3) {
			throw new AccountLockedException("Account is Locked");
		}
	}

	public void resetFailedLoginAttempts(String name)  {
		UserEntity user = userMapper.toEntityWithSecurityFields(getUserByName(name));
			user.setFailedCount(0L);
		}
}


