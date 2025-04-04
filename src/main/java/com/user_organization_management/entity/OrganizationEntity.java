package com.user_organization_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "organizations")
public class OrganizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must be required")
    @Column(unique = true, nullable = false)
    private String name;

//    cascade = CascadeType.ALL: Automatically deletes users when the organization is deleted.
//    orphanRemoval = true: Removes users from the database if they are removed from the list.
    @OneToMany(mappedBy = "organization")
    private Set<UserEntity> users;


    public OrganizationEntity(String name) {
        this.name = name;
    }
}
