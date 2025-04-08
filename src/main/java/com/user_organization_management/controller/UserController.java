package com.user_organization_management.controller;
import com.user_organization_management.model.CustomPageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.service.UserService;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<CustomPageResponse<UserDTO>> getAllUsers(
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String mobile,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(userService.getUsersByFilterWithPagination(email, mobile, page, size));
	}

	@RequestMapping(value = "/{id}" , method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


	@RequestMapping(value = "/email" , method = RequestMethod.GET)
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
		return ResponseEntity.ok(userService.getUserByEmail(email));
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UserDTO> updateUser(@PathVariable @Min(1) Long id, @RequestBody @Valid UserDTO userDTO) {
		return ResponseEntity.ok(userService.updateUser(id, userDTO));
	}

	@RequestMapping(value = "/{userId}/assign/{orgId}", method = RequestMethod.PUT)
	public ResponseEntity<UserDTO> assignUserToOrganization(@PathVariable Long userId, @PathVariable Long orgId) {
		return ResponseEntity.ok(userService.assignUserToOrganization(userId, orgId));
	}
	
	@RequestMapping(value = "/un-assign/{userId}" , method = RequestMethod.PUT)
	public ResponseEntity<UserDTO> unAssignUserFromOrganization(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.unassignUserFromOrganization(userId));
	}

	@RequestMapping(value = "/{id}" , method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
