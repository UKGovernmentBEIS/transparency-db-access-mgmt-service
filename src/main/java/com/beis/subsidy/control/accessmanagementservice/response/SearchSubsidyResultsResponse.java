package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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
	private List<AwardResponse> awards;

	public SearchSubsidyResultsResponse(List<Award> awards, long totalSearchResults,
										int currentPage, int totalPages) {

		this.awards = awards.stream().map(award ->
				new AwardResponse(award)).collect(Collectors.toList());
		this.totalSearchResults = totalSearchResults;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
	}
}
