package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.AuditLogs;
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
public class AuditLogsResultsResponse {

	public long totalSearchResults;
	public int currentPage;
	public int totalPages;

	@JsonProperty
	private List<AuditLogsResponse> auditLogs;

	public AuditLogsResultsResponse(List<AuditLogs> audits, long totalSearchResults,
										int currentPage, int totalPages) {

		this.auditLogs = audits.stream().map(audit ->
				new AuditLogsResponse(audit)).collect(Collectors.toList());
		this.totalSearchResults = totalSearchResults;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
	}
}
