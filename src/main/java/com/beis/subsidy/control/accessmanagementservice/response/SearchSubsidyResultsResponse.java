package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class SearchSubsidyResultsResponse {

	public long totalSearchResults;
	public int currentPage;
	public int totalPages;
	@JsonProperty
	private Map<String, Integer> awardStatusCounts;

	@JsonProperty
	private List<AwardResponse> awards;

	public SearchSubsidyResultsResponse(List<Award> awards, long totalSearchResults,
										int currentPage, int totalPages, Map<String, Integer> awardStatusCounts) {

		this.awards = awards.stream().map(award ->
				new AwardResponse(award)).collect(Collectors.toList());
		this.totalSearchResults = totalSearchResults;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.awardStatusCounts = awardStatusCounts;

	}
}
