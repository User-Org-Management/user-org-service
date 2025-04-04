package com.user_organization_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "name is required")
    private String name;


    @Email(message = "Invalid Email Format")
    @NotNull(message = "email is required")
    @Column(unique = true,  nullable = false)
    private String email;

    @NotNull(message = "Mobile Is Required")
    private String mobile;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = true)
    private OrganizationEntity organization;

    @NotNull(message = "password is required")
    private String password;

}
