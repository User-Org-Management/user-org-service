package com.user_organization_management.service;

import java.util.List;
import java.util.Optional;

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

	private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

	public List<OrganizationDTO> getAllOrganizations() {
	        return organizationRepository.findAll()
	                .stream()
	                .map(org -> new OrganizationDTO(org.getId(), org.getName()))
	                .toList();
	 }
	 public Optional<OrganizationDTO> getOrganizationById(Long id) {
		 	return organizationRepository.findById(id)
                .map(org -> new OrganizationDTO(org.getId(), org.getName()));
	 }


	public OrganizationDTO createOrganization(OrganizationDTO dto) {
		if (organizationRepository.existsByName(dto.getName())) {
			throw new IllegalArgumentException("Organization name already exists!");
		}
		OrganizationEntity organization = new OrganizationEntity(dto.getName());
		organization = organizationRepository.save(organization);
		return new OrganizationDTO(organization.getId(), organization.getName());
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
}
