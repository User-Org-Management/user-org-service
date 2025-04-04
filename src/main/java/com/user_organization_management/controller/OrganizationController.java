package com.user_organization_management.controller;

import java.util.List;

import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.user_organization_management.dto.OrganizationDTO;
import com.user_organization_management.service.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
		return ResponseEntity.ok(organizationService.getAllOrganizations());
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
		 return organizationService.getOrganizationById(id)
				 .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
		return ResponseEntity.ok(organizationService.createOrganization(organizationDTO));
	}

	@RequestMapping(value = "/update/{id}" , method = RequestMethod.PUT)
	public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable @Min(1) Long  id , @Valid @RequestBody OrganizationDTO organizationDTO) {
		return ResponseEntity.ok(organizationService.updateOrganization(id , organizationDTO));
	}

	@RequestMapping(value = "/delete/{id}" , method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteOrganization(@PathVariable @Min(1) Long id) {
		organizationService.deleteOrganization(id);
		return ResponseEntity.noContent().build();
	}
}
