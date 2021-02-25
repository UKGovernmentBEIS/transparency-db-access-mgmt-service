package com.beis.subsidy.control.accessmanagementservice.controller;

import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.GrantingAuthorityResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.SearchSubsidyResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.service.AccessManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.AccessManagementConstant;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(path = "/accessmanagement")
@RestController
@Slf4j
public class AccessManagementController {

    @Autowired
    private AccessManagementService accessManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        return new ResponseEntity<>("Successful health check - Access Management API", HttpStatus.OK);
    }
    @GetMapping("/beisadmin")
    public ResponseEntity<SearchResults> findBEISAdminDashboardData(@RequestHeader("userPrinciple") HttpHeaders userPrinciple) {
        UserPrinciple userPrincipleObj = null;
        //role validation
        userPrincipleObj = SearchUtils.validateRoleFromUserPrincipleObject(objectMapper,userPrinciple,
                AccessManagementConstant.BEIS_ADMIN_ROLE);
        try {
             SearchResults searchResults = accessManagementService.findBEISAdminDashboardData(userPrincipleObj);
            return new ResponseEntity<SearchResults>(searchResults, HttpStatus.OK);
        }
        catch(Exception e) {
            throw new SearchResultNotFoundException("Search Result not found");
        }
    }

    @GetMapping("/gaadmin")
    public ResponseEntity<SearchResults> findGAAdminDashboardData(@RequestHeader("userPrinciple") HttpHeaders userPrinciple)
            throws JsonProcessingException {
        UserPrinciple userPrincipleObj = null;
        //role validation
        userPrincipleObj = SearchUtils.validateRoleFromUserPrincipleObject(objectMapper,userPrinciple,
                AccessManagementConstant.GA_ADMIN_ROLE);
        try{
            SearchResults searchResults = accessManagementService.findGAAdminDashboardData(userPrincipleObj);
            return new ResponseEntity<SearchResults>(searchResults, HttpStatus.OK);
        }
        catch(SearchResultNotFoundException notFoundException) {
            throw new SearchResultNotFoundException("Search Result not found");
        }
    }
    @GetMapping("/gaapprover")
    public ResponseEntity<SearchResults> findGaApproverDashboardData(@RequestHeader("userPrinciple") HttpHeaders userPrinciple){
        log.info("Controller : Inside findGaApproverDashboardData method");
        UserPrinciple userPrincipleObj = null;
        //role validation
        userPrincipleObj = SearchUtils.validateRoleFromUserPrincipleObject(objectMapper,userPrinciple,
                AccessManagementConstant.GA_APPROVER_ROLE);
        try{
            SearchResults searchResults = accessManagementService.findGAApproverDashboardData(userPrincipleObj);
            return new ResponseEntity<SearchResults>(searchResults, HttpStatus.OK);
        }
        catch(SearchResultNotFoundException notFoundException) {
            log.error("Result not found");
            throw new SearchResultNotFoundException("Search Result not found");
        }
    }

    @GetMapping("/gaencoder")
    public ResponseEntity<SearchResults> findGaEncoderDashboardData(@RequestHeader("userPrinciple") HttpHeaders userPrinciple){
        log.info("{}::Controller : Inside findGaEncoderDashboardData method", loggingComponentName);

        UserPrinciple userPrincipleObj = SearchUtils.validateRoleFromUserPrincipleObject(objectMapper,userPrinciple,
                AccessManagementConstant.GA_ENCODER_ROLE);
        try{
            SearchResults searchResults = accessManagementService.findGAEncoderDashboardData(userPrincipleObj);
            return new ResponseEntity<SearchResults>(searchResults, HttpStatus.OK);
        }
        catch(SearchResultNotFoundException notFoundException) {
            log.error("{}:: Result not found", loggingComponentName);
            throw new SearchResultNotFoundException("Search Result not found");
        }
    }

    @GetMapping("/allga")
    public ResponseEntity<List<GrantingAuthorityResponse>> findAllGA(){
        List<GrantingAuthorityResponse> allGA = null;
        log.info("{}::Inside findAllGA method", loggingComponentName);

        allGA = accessManagementService.getAllGA();

        return new ResponseEntity<List<GrantingAuthorityResponse>>(allGA, HttpStatus.OK);
    }

    @GetMapping("/rolebasedgas")
    public ResponseEntity<List<GrantingAuthorityResponse>> findRoleBasedGAs(@RequestHeader("userPrinciple") HttpHeaders userPrinciple){
        List<GrantingAuthorityResponse> allGA = null;
        log.info("{}::Inside findAllGA method", loggingComponentName);
        UserPrinciple userPrincipleObj = SearchUtils.isRoleValid(objectMapper,userPrinciple);
        allGA = accessManagementService.getRoleBasedGAs(userPrincipleObj);

        return new ResponseEntity<List<GrantingAuthorityResponse>>(allGA, HttpStatus.OK);
    }

    @PutMapping(
            value = "/{awardNumber}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> updateSubsidyAward(@RequestHeader("userPrinciple") HttpHeaders userPrinciple,
                                                     @Valid @RequestBody UpdateAwardDetailsRequest awardUpdateRequest,
                                                     @PathVariable("awardNumber") Long awardNumber) {

         log.info("{}:: Before calling updateSubsidyAward::{}", loggingComponentName);
        UserPrinciple userPrincipleResp = SearchUtils.validateAdminGAApproverRoleFromUpObj(objectMapper,userPrinciple);
         if (StringUtils.isEmpty(awardNumber) || Objects.isNull(awardUpdateRequest)) {
              throw new InvalidRequestException("Bad Request AwardId is null or requestBody is null");
         }
        return accessManagementService.updateAwardDetailsByAwardId(awardNumber, awardUpdateRequest);
    }

    @GetMapping(
            value = "/searchresults",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SearchSubsidyResultsResponse> retrieveSubsidyAwardDetails(@RequestHeader("userPrinciple")
             HttpHeaders userPrinciple, @RequestParam(value = "searchName",required = false) String searchName,
             @RequestParam(value = "status",required = false) String status,
             @RequestParam(value = "page", required = false) Integer page,
             @RequestParam(value = "recordsPerPage", required = false) Integer recordsPerPage,
             @RequestParam(value = "sortBy", required = false)  String[] sortBy) {

        log.info("{}:: Before calling retrieveSubsidyAwardDetails::{}", loggingComponentName);
        UserPrinciple userPrincipleObj = SearchUtils.isRoleValid(objectMapper,userPrinciple);
        //Set Default Page records
        if(recordsPerPage == null) {
            recordsPerPage = 10;
        }

        if(page == null) {
            page = 1;
        }
        SearchSubsidyResultsResponse searchResults = accessManagementService.findMatchingSubsidyMeasureWithAwardDetails(
                searchName, status, page, recordsPerPage, userPrincipleObj, sortBy);
        return new ResponseEntity<SearchSubsidyResultsResponse>(searchResults, HttpStatus.OK);
    }
}