package com.beis.subsidy.control.accessmanagementservice.utils;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;

public final class AwardSpecificationUtils {

	public static Specification<Award> awardByGrantingAuthority(Long gaId) {
		return (root, query, builder) -> builder.equal(root.get("grantingAuthority").get("gaId"), gaId);
	}

	/**
	 * To define specification for award status
	 * @param status
	 * @return Specification<Award>
	 */
	public static Specification<Award> awardByStatus(String status) {
		return (root, query, builder) -> builder.equal(root.get("status"), status);
	}

	/**
	 * To define specification for subsidy measure title
	 *
	 * @param subsidyMeasureTitle - Add subsidy measure title
	 * @return Specification<Award> - Specification for Award
	 */
	public static Specification<Award> subsidyMeasureTitle(String subsidyMeasureTitle) {
		return (root, query, builder) -> builder.like(root.get("subsidyMeasure").get("subsidyMeasureTitle"), contains(subsidyMeasureTitle));
	}

	/**
	 * To define specification for subsidy measure title
	 *
	 * @param subsidyNumber - Add subsidy measure title
	 * @return Specification<Award> - Specification for Award
	 */
	public static Specification<Award> subsidyNumber(String subsidyNumber) {
		return (root, query, builder) -> builder.equal(root.get("subsidyMeasure").get("scNumber"), subsidyNumber);
	}

	/**
	 * To check contains operations
	 * @param expression - input string
	 * @return - message format with like expression
	 */
	private static String contains(String expression) {
		return MessageFormat.format("%{0}%", expression);
	}


	public static Specification<Award> grantingAuthorityName(String searchName) {

		return (root, query, builder) -> builder.like(root.get("grantingAuthority").get("grantingAuthorityName"),
				contains(searchName));
	}

	public static Specification<Award> beneficiaryName(String beneficiaryName) {

		return (root, query, builder) -> builder.like(builder.lower(root.get("beneficiary").get("beneficiaryName")),
				contains(beneficiaryName));
	}

    public static Specification<Award> awardByNumber(Long awardNumber) {
		return (root, query, builder) -> builder.equal(root.get("awardNumber"),awardNumber);
    }
}