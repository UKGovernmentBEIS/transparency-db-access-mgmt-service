package com.beis.subsidy.control.accessmanagementservice.service;

import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.AuditLogsResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.GrantingAuthorityResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.SearchSubsidyResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AccessManagementService {

    SearchResults findBEISAdminDashboardData(UserPrinciple userPrincipleObj);

    SearchResults findGAAdminDashboardData(UserPrinciple userPrincipleObj);

    ResponseEntity<Object> updateAwardDetailsByAwardId(Long awardId, String userName, UpdateAwardDetailsRequest awardUpdateRequest,String accessToken);

    SearchResults findGAApproverDashboardData(UserPrinciple userPrincipleObj);

    SearchResults findGAEncoderDashboardData(UserPrinciple userPrincipleObj);

    List<GrantingAuthorityResponse> getAllGA();

    List<GrantingAuthorityResponse> getRoleBasedGAs(UserPrinciple userPrincipleObj);

    SearchSubsidyResultsResponse findMatchingSubsidyMeasureWithAwardDetails(String searchName, String status, Integer page,
                        Integer recordsPerPage, UserPrinciple userPrinciple,String[] sortBy);
    
    AuditLogsResultsResponse findMatchingAuditLogDetails(UserPrinciple userPrinciple,String searchName,LocalDate searchStartDate,LocalDate searchEndDate, Integer page, Integer recordsPerPage, String[] sortBy);
}
