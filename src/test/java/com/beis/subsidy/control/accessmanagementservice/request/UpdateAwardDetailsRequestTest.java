package com.beis.subsidy.control.accessmanagementservice.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateAwardDetailsRequestTest {

    @Test
    public void testUpdateAwardRequest() {

        UpdateAwardDetailsRequest request = new UpdateAwardDetailsRequest();
        request.setOrgSize("small org");
        request.setReason("reason");
        request.setBeneficiaryName("bname");
        request.setGoodsOrServices("goods");
        request.setGrantingAuthorityName("authorityName");
        request.setNationalId("nationalId");
        request.setSpendingRegion("sregion");
        request.setSubsidyObjective("sobjective");
        request.setSpendingSector("ssector");
        request.setSubsidyAmountExact("100000");
        request.setSubsidyAmountRange("1000-10000");
        request.setLegalGrantingDate("12/01/2011");
        request.setStatus("draft");
        request.setSubsidyControlTitle("title");
        request.setNationalIdType("idType");
        request.setGrantingAuthorityName("gaName");
        request.setSubsidyInstrument("sInstrument");
        request.setPublishedAwardDate("01/01/2022");
        assertThat(request).isNotNull();
        assertThat(request.getOrgSize()).isEqualTo("small org");
        assertThat(request.getBeneficiaryName()).isEqualTo("bname");
        assertThat(request.getGoodsOrServices()).isEqualTo("goods");
        assertThat(request.getGrantingAuthorityName()).isEqualTo("gaName");
        assertThat(request.getLegalGrantingDate()).isEqualTo("12/01/2011");
        assertThat(request.getNationalId()).isEqualTo("nationalId");
        assertThat(request.getNationalIdType()).isEqualTo("idType");
        assertThat(request.getSpendingRegion()).isEqualTo("sregion");
        assertThat(request.getSpendingSector()).isEqualTo("ssector");
        assertThat(request.getSubsidyAmountExact()).isEqualTo("100000");
        assertThat(request.getSubsidyAmountRange()).isEqualTo("1000-10000");
        assertThat(request.getSubsidyControlTitle()).isEqualTo("title");
        assertThat(request.getSubsidyInstrument()).isEqualTo("sInstrument");
        assertThat(request.getSubsidyObjective()).isEqualTo("sobjective");
        assertThat(request.getReason()).isEqualTo("reason");
        assertThat(request.getPublishedAwardDate()).isEqualTo("01/01/2022");

    }
}
