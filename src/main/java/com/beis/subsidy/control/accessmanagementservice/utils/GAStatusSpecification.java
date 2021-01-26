package com.beis.subsidy.control.accessmanagementservice.utils;

import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import org.springframework.data.jpa.domain.Specification;

public class GAStatusSpecification {
    public static Specification<GrantingAuthority> grantingAuthorityStatus(String status) {
        return (root, query, builder) -> builder.notEqual(root.get("status"), status);
    }
}
