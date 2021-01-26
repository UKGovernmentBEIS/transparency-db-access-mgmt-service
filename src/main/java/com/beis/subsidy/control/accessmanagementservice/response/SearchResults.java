package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * Search results object - Represents search results for award search
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResults {
	@JsonProperty
	private Map<String, Integer> grantingAuthorityUserActionCount;

	@JsonProperty
	private Map<String, Integer> awardUserActionCount;

	@JsonProperty
	private Map<String, Integer> subsidyMeasureUserActionCount;

	@JsonProperty
	private List<AwardResponse> awardResponse;

	@JsonProperty
	private List<SubsidyMeasureResponse> subsidyMeasureResponse;

	@JsonProperty
	private List<GrantingAuthorityResponse> grantingAuthorityResponse;

	public SearchResults(List<GrantingAuthority> grantingAuthorityResponses, Map<String, Integer> userActionCount) {
		this.grantingAuthorityResponse = grantingAuthorityResponses.stream().map(grantingAuthorityResponse ->
				new GrantingAuthorityResponse(grantingAuthorityResponse, userActionCount))
				.collect(Collectors.toList());
		this.grantingAuthorityUserActionCount = userActionCount;
	}

	public SearchResults(Map<String, Integer> userActionCount) {
		this.grantingAuthorityUserActionCount = userActionCount;
	}
}
