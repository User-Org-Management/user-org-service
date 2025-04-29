package com.user_organization_management.service;

import java.util.List;

import com.user_organization_management.exception.DuplicateRecordException;
import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.model.SearchModel;
import com.user_organization_management.repository.OrganizationRepository;
import com.user_organization_management.repository.UserRepository;

import static com.user_organization_management.utils.Constants.*;

import com.user_organization_management.specification.OrganizationSpecification;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

	private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

	@Autowired
	private  OrganizationRepository organizationRepository;
	@Autowired
	private  UserRepository userRepository;
	@Autowired
	private  OrganizationMapper organizationMapper;

	public List<OrganizationDTO> getAllOrganizations(String name) {
		Specification<OrganizationEntity> spec = OrganizationSpecification.filterByName(name);
		logger.info(LOG_FETCH_ALL_ORGS);
		return organizationRepository.findAll(spec)
				.stream()
				.map(organizationMapper::toDTO)
				.toList();
	}


	public List<OrganizationDTO> filterOrganizations(SearchModel searchModel) {
		OrganizationSpecification spec = new OrganizationSpecification(searchModel);
		logger.info(LOG_FETCH_ALL_ORGS);
		return organizationRepository.findAll(spec)
				.stream()
				.map(organizationMapper::toDTO)
				.toList();
	}



	public OrganizationDTO getOrganizationById(Long id) {
		logger.info(LOG_FETCH_ORG_BY_ID, id);
		return organizationRepository.findById(id)
				.map(organizationMapper::toDTO)
				.orElseThrow(() -> new EntityNotFoundException(String.format(ORG_NOT_FOUND_WITH_ID, String.valueOf(id))));
	}

	public OrganizationDTO create(OrganizationDTO body) {
		logger.info(LOG_CREATE_ORG, body.getName());
		validateOrganizationNameUniqueness(body, body.getId());
		OrganizationEntity entity = organizationMapper.toEntity(body);
		OrganizationEntity savedEntity = organizationRepository.save(entity);
		logger.info(LOG_ORG_CREATED, savedEntity.getId());
		return organizationMapper.toDTO(savedEntity);
	}

	public OrganizationDTO update(Long orgId, OrganizationDTO dto) {
		logger.info(LOG_UPDATE_ORG, orgId);
		OrganizationEntity organization = organizationMapper.toEntity(getOrganizationById(orgId));
		validateOrganizationNameUniqueness(dto, orgId);
		organization.setName(dto.getName());
		OrganizationEntity organizationSaved = organizationRepository.save(organization);
		logger.info(LOG_ORG_UPDATED, organizationSaved.getId());
		return organizationMapper.toDTO(organizationSaved);
	}

	@Transactional
	public void deleteOrganization(Long id) {
		logger.info(LOG_DELETE_ORG, id);
		OrganizationEntity organization = organizationMapper.toEntity(getOrganizationById(id));
		List<UserEntity> users = userRepository.findByOrganizationId(id);
		if (!users.isEmpty()) {
			logger.info(LOG_DELETE_ORG_USERS, users.size(), id);
			userRepository.deleteAll(users);
		}
		organizationRepository.delete(organization);
		logger.info(LOG_ORG_DELETED, id);
	}

	public void validateOrganizationNameUniqueness(OrganizationDTO body, Long id) {
		if (isNameTaken(body.getName(), id)) {
			logger.warn(LOG_ORG_NAME_EXISTS, body.getName());
			throw new DuplicateRecordException(String.format(ORG_NAME_EXISTS, body.getName()));
		}
	}

	private boolean isNameTaken(String name, Long id) {
		if (id == null) {
			return organizationRepository.existsByName(name);
		} else {
			return !getOrganizationById(id).getName().equals(name) && organizationRepository.existsByName(name);
		}
	}
}
