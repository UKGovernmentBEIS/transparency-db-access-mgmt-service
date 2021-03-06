package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.Beneficiary;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BeneficiaryResponse {

    @JsonProperty
    private String beneficiaryName;

    public BeneficiaryResponse(Beneficiary beneficiary) {

        this.beneficiaryName  = beneficiary.getBeneficiaryName();
    }
}
