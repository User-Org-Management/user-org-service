package com.user_organization_management.controller;
import com.user_organization_management.model.CustomPageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<CustomPageResponse<UserDTO>> getAllUsers(
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String mobile,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(userService.getUsersByFilterWithPagination(email, mobile, page, size));
	}

	@GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


	@GetMapping("/email")
	public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
		return userService.getUserByEmail(email)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}


	@PutMapping("/{userId}/assign/{orgId}")
    public ResponseEntity<UserDTO> assignUserToOrganization(@PathVariable Long userId, @PathVariable Long orgId) {
        return ResponseEntity.ok(userService.assignUserToOrganization(userId, orgId));
    }
	
	@PutMapping("/un-assign/{userId}")
	public ResponseEntity<UserDTO> unAssignUserFromOrganization(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.unassignUserFromOrganization(userId));
	}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

//	@RequestMapping(value = "/update-user/{id}" , method = RequestMethod.PUT)
	@PutMapping("/update-user/{id}")
	public ResponseEntity<UserDTO> updateOrganization(@PathVariable @Min(1) Long  id , @Valid @RequestBody UserDTO userDTO) {
		return ResponseEntity.ok(userService.updateUser(id , userDTO));
	}
}
