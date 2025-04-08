package com.user_organization_management.specification;

import com.user_organization_management.entity.OrganizationEntity;
import org.springframework.data.jpa.domain.Specification;
import com.user_organization_management.utils.BaseSpecifications;

public class OrganizationSpecification {

    public static Specification<OrganizationEntity> filterByName(String name) {
        return BaseSpecifications.nameContains(name);
    }
}
