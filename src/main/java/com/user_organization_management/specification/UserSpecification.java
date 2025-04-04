package com.user_organization_management.specification;

import com.user_organization_management.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

    public static Specification<UserEntity> filterByEmailAndMobile(String email, String mobile) {
        return (root, query, criteriaBuilder) -> {
            Specification<UserEntity> spec = Specification.where(null);
            if (StringUtils.hasText(email) && StringUtils.hasText(mobile)) {
                spec = spec.and(emailContains(email)).and(mobileContains(mobile));
            } else if (StringUtils.hasText(email)) {
                spec = spec.and(emailEquals(email));
            } else if (StringUtils.hasText(mobile)) {
                spec = spec.and(mobileContains(mobile));
            }
            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }

    private static Specification<UserEntity> emailEquals(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    private static Specification<UserEntity> emailContains(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("email"), "%" + email + "%");
    }

    private static Specification<UserEntity> mobileContains(String mobile) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("mobile"), "%" + mobile + "%");
    }

    public static Specification<UserEntity> filterByName(String name) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(name) ? criteriaBuilder.like(root.get("name"), "%" + name + "%") : null;
    }
}