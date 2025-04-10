package com.user_organization_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank(message = "name is required")
    @Column(unique = true, nullable = false)
    private String name;
    @Email(message = "Invalid Email Format")
    @NotBlank(message = "Email is required")
    @Column(unique = true,  nullable = false)
    private String email;
    @NotBlank(message = "Mobile Is Required")
    private String mobile;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long organizationId;
    private String organizationName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long roleId;
    private String roleName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Long failedCount;
    private boolean locked;
}
