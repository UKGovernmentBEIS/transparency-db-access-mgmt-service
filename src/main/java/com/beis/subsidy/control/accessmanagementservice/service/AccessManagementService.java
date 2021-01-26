package com.beis.subsidy.control.accessmanagementservice.service;

import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.GrantingAuthorityResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.SearchSubsidyResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccessManagementService {

    SearchResults findBEISAdminDashboardData(UserPrinciple userPrincipleObj);

    SearchResults findGAAdminDashboardData(UserPrinciple userPrincipleObj);

    ResponseEntity<Object> updateAwardDetailsByAwardId(Long awardId, UpdateAwardDetailsRequest awardUpdateRequest);

    SearchResults findGAApproverDashboardData(UserPrinciple userPrincipleObj);

    SearchResults findGAEncoderDashboardData(UserPrinciple userPrincipleObj);

    List<GrantingAuthorityResponse> getAllGA();

    SearchSubsidyResultsResponse findMatchingSubsidyMeasureWithAwardDetails(String searchName, String status, Integer page, Integer recordsPerPage);
}
