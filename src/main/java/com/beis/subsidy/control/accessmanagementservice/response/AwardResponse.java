package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AwardResponse {
    @JsonProperty
    private Long awardNumber;

    @JsonProperty
    private String subsidyFullAmountExact;

    @JsonProperty
    private String subsidyFullAmountRange;

    @JsonProperty
    private String subsidyObjective;

    @JsonProperty
    private String subsidyMeasureTitle;

    @JsonProperty
    private String status;

    @JsonProperty
    private String gaName;

    @JsonProperty
    private String lastModifiedDate;

    @JsonProperty
    private String scNumber;

    @JsonProperty
    private String subsidyInstrument;

    @JsonProperty
    private String beneficiaryName;

    @JsonProperty
    private String reason;

    @JsonProperty
    private String subsidyAwardInterest;

    @JsonProperty
    private String SPEI;

    @JsonProperty
    private String legalBasis;

    @JsonProperty
    private String standaloneAwardTitle;

    public AwardResponse(Award award) {
        this.awardNumber = award.getAwardNumber();
        this.subsidyFullAmountRange = SearchUtils.formatedFullAmountRange(award.getSubsidyFullAmountRange());
        this.subsidyFullAmountExact = SearchUtils.decimalNumberFormat(award.getSubsidyFullAmountExact());
        this.status = award.getStatus();
        this.gaName = award.getGrantingAuthority().getGrantingAuthorityName();
        this.lastModifiedDate = SearchUtils.dateToFullMonthNameInDate(award.getLastModifiedTimestamp());
        this.subsidyObjective = award.getSubsidyObjective();
        if(award.getSubsidyMeasure() == null) {
            this.subsidyMeasureTitle = "NA";
            this.scNumber = "NA";
        }else{
            this.subsidyMeasureTitle = award.getSubsidyMeasure().getSubsidyMeasureTitle();
            this.scNumber = award.getSubsidyMeasure().getScNumber();
        }
        this.subsidyInstrument = award.getSubsidyInstrument();
        this.beneficiaryName = award.getBeneficiary().getBeneficiaryName();
        this.reason =award.getReason();
        this.subsidyAwardInterest = award.getSubsidyAwardInterest();
        this.SPEI = award.getSPEI();
        this.legalBasis = award.getLegalBasis();
        this.standaloneAwardTitle = award.getStandaloneAwardTitle();
    }
}
