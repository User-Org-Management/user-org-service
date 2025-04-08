package com.user_organization_management.repository;

import com.user_organization_management.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<OrganizationEntity> {
    boolean existsByName(String name);
}