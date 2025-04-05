package com.user_organization_management.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "name is required")
    private String name;
    @Email(message = "Invalid Email Format")
    @NotBlank(message = "email is required")
    @Column(unique = true,  nullable = false)
    private String email;
    @NotBlank(message = "Mobile Is Required")
    private String mobile;
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = true)
    private OrganizationEntity organization;
    @NotBlank(message = "password is required")
    private String password;

}
