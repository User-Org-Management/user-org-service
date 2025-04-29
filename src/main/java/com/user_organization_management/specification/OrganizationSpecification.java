package com.user_organization_management.specification;

import com.user_organization_management.entity.OrganizationEntity;
import com.user_organization_management.model.SearchModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import com.user_organization_management.utils.BaseSpecifications;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class OrganizationSpecification implements Specification<OrganizationEntity> {

    @Autowired
    private SearchModel orgSearch;

    public static Specification<OrganizationEntity> filterByName(String name) {
        return BaseSpecifications.nameContains(name);
    }

    @Override
    public Predicate toPredicate(Root<OrganizationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (orgSearch.getName() !=null && !orgSearch.getName().isEmpty()){
            predicates.add(cb.like(root.get("name"), orgSearch.getName()));
        }
        if (orgSearch.getId() !=null){
            predicates.add(cb.equal(root.get("id"), orgSearch.getId()));
        }
//        if (orgSearch.getName() !=null && !orgSearch.getName().isEmpty()){
//            predicates.add(cb.like(root.get("name"), orgSearch.getName()));
//        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
