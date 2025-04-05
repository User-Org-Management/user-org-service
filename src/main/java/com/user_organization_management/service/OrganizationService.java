package com.user_organization_management.service;

import java.util.List;

import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.repository.OrganizationRepository;
import com.user_organization_management.repository.UserRepository;
import static com.user_organization_management.util.Constants.*;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

	private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

	private final OrganizationRepository organizationRepository;
	private final UserRepository userRepository;
	private final OrganizationMapper organizationMapper = OrganizationMapper.INSTANCE;

	public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
		this.organizationRepository = organizationRepository;
		this.userRepository = userRepository;
	}

	public List<OrganizationDTO> getAllOrganizations() {
		logger.info("Fetching all organizations");
		return organizationRepository.findAll()
				.stream()
				.map(organizationMapper::toDTO)
				.toList();
	}

	public OrganizationDTO getOrganizationById(Long id) {
		logger.info("Fetching organization by ID: {}", id);
		return organizationRepository.findById(id)
				.map(organizationMapper::toDTO)
				.orElseThrow(() -> new EntityNotFoundException(ORG_NOT_FOUND));
	}

	public OrganizationDTO createOrganization(OrganizationDTO body) {
		logger.info("Creating organization with name: {}", body.getName());
		validateOrganizationNameUniqueness(body.getName());
		OrganizationEntity entity = organizationMapper.toEntity(body);
		OrganizationEntity savedEntity = organizationRepository.save(entity);
		logger.info("Organization created with ID: {}", savedEntity.getId());
		return organizationMapper.toDTO(savedEntity);
	}

	public OrganizationDTO updateOrganization(Long id, OrganizationDTO dto) {
		logger.info("Updating organization with ID: {}", id);
		OrganizationEntity organization = organizationRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ORG_NOT_FOUND));

		if (!organization.getName().equals(dto.getName()) &&
				organizationRepository.existsByName(dto.getName())) {
			logger.warn("Organization name already exists: {}", dto.getName());
			throw new IllegalArgumentException(ORG_NAME_EXISTS);
		}
		organization.setName(dto.getName());
		organization = organizationRepository.save(organization);
		logger.info("Organization updated with ID: {}", organization.getId());
		return organizationMapper.toDTO(organization);
	}

	@Transactional
	public void deleteOrganization(Long id) {
		logger.info("Deleting organization with ID: {}", id);
		OrganizationEntity organization = organizationRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(String.format(ORG_WITH_ID_NOT_FOUND, id)));

		List<UserEntity> users = userRepository.findByOrganizationId(id);
		if (!users.isEmpty()) {
			logger.info("Deleting {} users assigned to organization ID: {}", users.size(), id);
			userRepository.deleteAll(users);
		}
		organizationRepository.delete(organization);
		logger.info("Organization with ID {} deleted", id);
	}

	private void validateOrganizationNameUniqueness(String name) {
		if (organizationRepository.existsByName(name)) {
			logger.warn("Duplicate organization name found: {}", name);
			throw new IllegalArgumentException(ORG_NAME_EXISTS);
		}
	}
}
