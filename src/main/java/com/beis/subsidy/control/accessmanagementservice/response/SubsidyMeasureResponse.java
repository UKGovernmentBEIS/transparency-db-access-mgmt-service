package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.SubsidyMeasure;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubsidyMeasureResponse {

    @JsonProperty
    private String subsidyMeasureTitle;

    @JsonProperty
    private String scNumber;

    @JsonProperty
    private LocalDate startDate;

    @JsonProperty
    private LocalDate endDate;

    @JsonProperty
    private String duration;

    @JsonProperty
    private String budget;

    @JsonProperty
    private String gaName;

    @JsonProperty
    private String lastModifiedDate;


    public SubsidyMeasureResponse(SubsidyMeasure subsidyMeasure) {
        this.scNumber = subsidyMeasure.getScNumber();
        this.subsidyMeasureTitle  = subsidyMeasure.getSubsidyMeasureTitle();
        this.startDate = subsidyMeasure.getStartDate();
        this.endDate = subsidyMeasure.getEndDate();
        this.duration = SearchUtils.getDurationInYears(subsidyMeasure.getDuration());
        this.budget = subsidyMeasure.getBudget();
        this.gaName = subsidyMeasure.getGrantingAuthority().getGrantingAuthorityName();
        this.lastModifiedDate = SearchUtils.dateToFullMonthNameInDate(subsidyMeasure.getLastModifiedTimestamp());
    }

}
