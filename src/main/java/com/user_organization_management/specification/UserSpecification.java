package com.user_organization_management.specification;

import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.utils.BaseSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

    public static Specification<UserEntity> filterByEmailAndMobile(String email, String mobile) {
        return (root, query, criteriaBuilder) -> {
            Specification<UserEntity> spec = Specification.where(null);
            if (StringUtils.hasText(email) && StringUtils.hasText(mobile)) {
                spec = spec.and(BaseSpecifications.emailContains(email)).and(BaseSpecifications.mobileContains(mobile));
            } else if (StringUtils.hasText(email)) {
                spec = spec.and(BaseSpecifications.emailContains(email));
            } else if (StringUtils.hasText(mobile)) {
                spec = spec.and(BaseSpecifications.mobileContains(mobile));
            }
            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}
