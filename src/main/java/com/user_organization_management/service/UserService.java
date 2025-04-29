package com.user_organization_management.service;

import java.util.List;
import java.util.Optional;

import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.RoleEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.AccountLockedException;
import com.user_organization_management.exception.BadCredentialsAuthException;
import com.user_organization_management.exception.DuplicateRecordException;
import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.mapper.UserMapper;
import com.user_organization_management.model.CustomPageResponse;
import com.user_organization_management.repository.RoleRepository;
import com.user_organization_management.specification.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class UserService {
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

	public UserDTO findUserById(Long id) {
		return userRepository.findById(id)
				.map(userMapper::toDTO)
				.orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND, String.valueOf(id))));
	}

//	public UserDTO getUserByReferenceId(Long id){
//		return userMapper.toDTO(userRepository.getReferenceById(id));
//	}

	@Transactional
	public UserDTO create(UserDTO userDTO) {
		log.info(USER_CREATION, userDTO.getEmail());
		checkUserEmailExist(userDTO.getEmail(), userDTO.getId());
		checkUserNameExist(userDTO, userDTO.getId());
		UserEntity user = userMapper.toEntity(userDTO);
		if (userDTO.getOrganizationId() != null) {
			OrganizationEntity organization = getOrganizationEntityById(userDTO.getOrganizationId());

			user.setOrganization(organization);

			log.info(USER_ASSIGNED_TO_ORG, userDTO.getEmail(), userDTO.getOrganizationId());
		} else {
			user.setOrganization(null);
			log.info(USER_NOT_ASSIGNED_TO_ORG, userDTO.getEmail());
		}
		if (userDTO.getRoleId() != null) {
			RoleEntity role = roleRepository.findById(userDTO.getRoleId())
					.orElseThrow(() -> new EntityNotFoundException("Role not found"));
			user.setRole(role);
		}else{
			RoleEntity defaultRole = roleRepository.findByName("USER").get();
			user.setRole(defaultRole);
		}

		String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
		user.setPassword(encodedPassword);
		log.info(PASSWORD_ENCODED, userDTO.getEmail());

		UserDTO savedUserDto = userMapper.toDTO(userRepository.save(user));
		log.info(USER_CREATED, savedUserDto.getEmail(), savedUserDto.getId());
		return savedUserDto;
	}

	public UserDTO update(Long userId, UserDTO userDTO) {
		log.info(USER_UPDATING, userId);
		UserEntity existingUser = userMapper.toEntity(findUserById(userId));

		checkUserEmailExist(userDTO.getEmail(), userId);
		checkUserNameExist(userDTO, userId);

		userMapper.updateEntityFromDTO(userDTO, existingUser) ;

		if (userDTO.getOrganizationId() != null) {
			OrganizationEntity organization = getOrganizationEntityById(userDTO.getOrganizationId());
			existingUser.setOrganization(organization);
			log.info(USER_ASSIGNED_TO_ORG_UPDATE, userDTO.getEmail(), userDTO.getOrganizationId());
		} else {
			existingUser.setOrganization(null);
			log.info(USER_UNASSIGNED_FROM_ORG_UPDATE, userDTO.getEmail());
		}

		if (userDTO.getRoleId() != null) {
			RoleEntity role = roleRepository.findById(userDTO.getRoleId())
					.orElseThrow(() -> new EntityNotFoundException("Role not found"));
			existingUser.setRole(role);
		} else {
			RoleEntity defaultRole = roleRepository.findByName("USER")
					.orElseThrow(() -> new EntityNotFoundException("Default USER role not found"));
			existingUser.setRole(defaultRole);
		}



		if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
			existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
			log.info(PASSWORD_UPDATED, userDTO.getEmail());
		}

		UserEntity updatedUser = userRepository.save(existingUser);
		log.info(USER_UPDATED, updatedUser.getEmail());

		return userMapper.toDTO(updatedUser);
	}

	@Transactional
	public UserDTO assignUserToOrganization(Long userId, Long orgId) {
		UserEntity user = userMapper.toEntity(findUserById(userId));
		OrganizationEntity org = getOrganizationEntityById(orgId);
		user.setOrganization(org);
		log.info(USER_ASSIGNING_ORG, userId, orgId);
		return userMapper.toDTO(userRepository.save(user));
	}

	public UserDTO unassignUserFromOrganization(Long userId) {
		log.info(USER_UNASSIGNED, userId);
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND, userId)));
		user.setOrganization(null);
		log.info(USER_UNASSIGNED_LOG, user);
		return userMapper.toDTO(userRepository.save(user));
	}

	public void deleteUser(Long id) {
		log.info(USER_DELETION, id);
		String idMs = id.toString();
		UserEntity user = userRepository.findById(id).orElseThrow(() ->
				new EntityNotFoundException(String.format(USER_NOT_FOUND, idMs)));

		log.info(USER_DELETED, user);
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

	public OrganizationEntity getOrganizationEntityById(Long orgId){
		return organizationMapper.toEntity(organizationService.getOrganizationById(orgId));
	}

	public UserEntity addOrganizationIfNotNull(Long userId, Long orgId){
		UserEntity existingUser = userMapper.toEntity(findUserById(userId));
		if (orgId != null) {
			OrganizationEntity organization = getOrganizationEntityById(orgId);
			existingUser.setOrganization(organization);
		} else {
			existingUser.setOrganization(null);
		}
		return existingUser;
	}

	public void checkUserEmailExist(String email, Long userId){
		Optional<UserEntity> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
			log.warn(EMAIL_EXISTS, email);
			throw new DuplicateRecordException(String.format(EMAIL_EXISTS, email));
		}
	}


	public void checkUserNameExist(UserDTO body, Long id) {
		if (isNameTaken(body.getName(), id)) {
			log.warn("User name '{}' already exists.", body.getName());
			throw new DuplicateRecordException(String.format(USER_NAME_EXISTS, body.getName()));
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
	public void increaseFailedLoginAttempts(String email) {
		UserEntity user = userMapper.toEntityWithSecurityFields(getUserByEmail(email));
		long count = user.getFailedCount();
		if (count >= 2) {
			user.setLocked(true);
			user.setFailedCount(count + 1);

		} else {
			user.setFailedCount(count + 1);
		}
		userRepository.save(user);
	}

	public void disableLogin(String email){
		UserEntity user = userMapper.toEntityWithSecurityFields(getUserByEmail(email));
		if (user.isLocked() && user.getFailedCount() >= 3) {
			throw new AccountLockedException("Account is Locked");
		}
	}

	public void resetFailedLoginAttempts(String email)  {
		UserEntity user = userMapper.toEntityWithSecurityFields(getUserByEmail(email));
			user.setFailedCount(0L);
		}
}


