package com.user_organization_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    @NotNull(message = "name is required")
    private String name;
    @Email(message = "Invalid Email Format")
    @NotNull(message = "Email is required")
    @Column(unique = true,  nullable = false)
    private String email;
    @NotNull(message = "Mobile Is Required")
    private String mobile;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long organizationId;
    private String organizationName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
