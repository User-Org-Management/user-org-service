package com.user_organization_management.controller;

import java.util.List;

import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
	public ResponseEntity<List<OrganizationDTO>> getAllOrganizations(@RequestParam(required = false) String name) {
		return ResponseEntity.ok(organizationService.getAllOrganizations(name));
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
		 return ResponseEntity.ok(organizationService.getOrganizationById(id));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<OrganizationDTO> createOrganization(@RequestBody @Valid  OrganizationDTO organizationDTO) {
		return ResponseEntity.ok(organizationService.createOrganization(organizationDTO));
	}

	@RequestMapping(value = "/update/{id}" , method = RequestMethod.PUT)
	public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable @Min(1) Long  id, @RequestBody @Valid  OrganizationDTO organizationDTO) {
		return ResponseEntity.ok(organizationService.updateOrganization(id , organizationDTO));
	}

	@RequestMapping(value = "/delete/{id}" , method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteOrganization(@PathVariable @Min(1) Long id) {
		organizationService.deleteOrganization(id);
		return ResponseEntity.noContent().build();
	}
}
