package com.beis.subsidy.control.accessmanagementservice.utils;

import com.beis.subsidy.control.accessmanagementservice.model.SubsidyMeasure;
import org.springframework.data.jpa.domain.Specification;

public final class SubsidyMeasureSpecificationUtils {

public static Specification<SubsidyMeasure> subsidyMeasureByGrantingAuthority(Long gaId) {
	return (root, query, builder) -> builder.equal(root.get("grantingAuthority").get("gaId"), gaId);
}
}
