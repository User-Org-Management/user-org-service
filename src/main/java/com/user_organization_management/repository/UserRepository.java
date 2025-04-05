package com.user_organization_management.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.user_organization_management.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
	 Optional<UserEntity> findByName(String name);
	 boolean existsByEmail(String name);
	 List<UserEntity> findByOrganizationId(Long organizationId);

	@Query(value = "SELECT u FROM #{#entityName} u WHERE u.email = :email")
	Optional<UserEntity> findByEmail(String email);


}