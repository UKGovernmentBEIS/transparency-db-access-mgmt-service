package com.beis.subsidy.control.accessmanagementservice.utils;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import org.springframework.data.jpa.domain.Specification;

public class GACreatedBySpecification {
    public static Specification<GrantingAuthority> grantingAuthorityCreatedBy(String createdBy) {
        return (root, query, builder) -> builder.equal(root.get("createdBy"), createdBy);
    }
}
