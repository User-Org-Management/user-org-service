package com.user_organization_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.user_organization_management.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity<Long> {

    @NotBlank(message = "name is required")
    @Column(unique = true, nullable = false)
    private String name;

    @Email(message = "Invalid Email Format")
    @NotBlank(message = "email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mobile Is Required")
    private String mobile;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    private String password;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long failedCount;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean locked;

    @PrePersist
    public void prePersist() {
        if (failedCount == null) failedCount = 0L;
    }
}
