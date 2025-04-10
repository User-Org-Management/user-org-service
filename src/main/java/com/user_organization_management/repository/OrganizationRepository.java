package com.user_organization_management.repository;

import com.user_organization_management.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long>, JpaSpecificationExecutor<OrganizationEntity> {
    boolean existsByName(String name);
}