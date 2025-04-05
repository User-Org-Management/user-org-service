package com.user_organization_management.service;

import java.util.List;
import java.util.Optional;

import com.user_organization_management.mapper.OrganizationMapper;
import com.user_organization_management.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.repository.OrganizationRepository;
import com.user_organization_management.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrganizationService {
	@Autowired 
	private OrganizationRepository organizationRepository;
	@Autowired
	private UserRepository userRepository;

	private final OrganizationMapper organizationMapper = OrganizationMapper.INSTANCE;

	private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

	public List<OrganizationDTO> getAllOrganizations() {
	        return organizationRepository.findAll().stream().map(organizationMapper::toDTO).toList();
	 }
	 public Optional<OrganizationDTO> getOrganizationById(Long id) {
		 	return organizationRepository.findById(id).map(organizationMapper::toDTO);
	 }


	public OrganizationDTO createOrganization(OrganizationDTO body) {
		validateOrganizationNameUniqueness(body.getName());
		OrganizationEntity entity = organizationMapper.toEntity(body);
		OrganizationEntity savedEntity = organizationRepository.save(entity);
		return organizationMapper.toDTO(savedEntity);
	}



	public OrganizationDTO updateOrganization(Long id, OrganizationDTO dto) {
		OrganizationEntity organization = organizationRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Organization not found!"));
		if (!organization.getName().equals(dto.getName()) && organizationRepository.existsByName(dto.getName())) {
			throw new IllegalArgumentException("Organization name already exists!");
		}
		organization.setName(dto.getName());
		organization = organizationRepository.save(organization);
		return new OrganizationDTO(organization.getId(), organization.getName());
	}

	@Transactional
	public void deleteOrganization(Long id) {
		OrganizationEntity organization = organizationRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Organization with ID " + id + " not found!"));
		List<UserEntity> users = userRepository.findByOrganizationId(id);
		if (!users.isEmpty()) {
			userRepository.deleteAll(users);  
		}
		organizationRepository.delete(organization);
	}

	private void validateOrganizationNameUniqueness(String name) {
		if (organizationRepository.existsByName(name)) {
			throw new IllegalArgumentException("Organization name already exists!");
		}
	}



}
