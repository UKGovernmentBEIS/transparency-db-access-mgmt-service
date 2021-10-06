package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrantingAuthorityResponse {
    @JsonProperty
    private Long gaId;

    @JsonProperty
    private String gaName;

    @JsonProperty
    private String legalBasis;

    @JsonProperty
    private String status;

    @JsonProperty
    private String lastModifiedDate;

    @JsonProperty
    private String azGrpId;

    public GrantingAuthorityResponse(GrantingAuthority grantingAuthority, Map<String, Integer> userActionCount){
        this.gaId = grantingAuthority.getGaId();
        this.gaName = grantingAuthority.getGrantingAuthorityName();
        this.status = grantingAuthority.getStatus();
        this.lastModifiedDate = SearchUtils.dateToFullMonthNameInDate(grantingAuthority.getLastModifiedTimestamp());
        this.azGrpId = grantingAuthority.getAzureGroupId();
    }

}
