package com.beis.subsidy.control.accessmanagementservice.controller;

import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.exception.UnauthorisedAccessException;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.GrantingAuthorityResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.SearchSubsidyResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.service.AccessManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.AccessManagementConstant;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        return new ResponseEntity<>("Successful health check - Access Management API", HttpStatus.OK);
    }
    @GetMapping("/beisadmin")
    public ResponseEntity<SearchResults> findBEISAdminDashboardData(@RequestHeader("userPrinciple") HttpHeaders userPrinciple) {
        UserPrinciple userPrincipleObj = null;
        String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
        try {
            userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
            if(! userPrincipleObj.getRole().equals(AccessManagementConstant.BEIS_ADMIN_ROLE)){
                throw new UnauthorisedAccessException("You are not authorised to view BEIS Admin Dashboard ");
            }
        } catch (JsonProcessingException e) {}
        try {
             SearchResults searchResults = accessManagementService.findBEISAdminDashboardData(userPrincipleObj);
            return new ResponseEntity<SearchResults>(searchResults, HttpStatus.OK);
        }
        catch(Exception e) {
            throw new SearchResultNotFoundException("Search Result not found");
        }
    }

    @GetMapping("/gaadmin")
    public ResponseEntity<SearchResults> findGAAdminDashboardData(@RequestHeader("userPrinciple") HttpHeaders userPrinciple) throws JsonProcessingException {
        UserPrinciple userPrincipleObj = null;
        String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
        try {
            userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
            if(! userPrincipleObj.getRole().equals(AccessManagementConstant.GA_ADMIN_ROLE)){
                throw new UnauthorisedAccessException("You are not authorised to view GA Admin Dashboard ");
            }
        } catch (JsonProcessingException e) {}
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
        String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
        try {
            userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
            if(! userPrincipleObj.getRole().equals(AccessManagementConstant.GA_APPROVER_ROLE)){
                log.error("Incorrect role received for GA Approver: "+userPrincipleObj.getRole());
                throw new UnauthorisedAccessException("You are not authorised to view GA Approver Dashboard ");
            }
        } catch (JsonProcessingException e) {}
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
        log.info("Controller : Inside findGaApproverDashboardData method");
        UserPrinciple userPrincipleObj = null;
        String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
        try {
            userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
            if(! userPrincipleObj.getRole().equals(AccessManagementConstant.GA_ENCODER_ROLE)){
                log.error("Incorrect role received for GA Approver: "+userPrincipleObj.getRole());
                throw new UnauthorisedAccessException("You are not authorised to view GA Approver Dashboard ");
            }
        } catch (JsonProcessingException e) {}
        try{
            SearchResults searchResults = accessManagementService.findGAEncoderDashboardData(userPrincipleObj);
            return new ResponseEntity<SearchResults>(searchResults, HttpStatus.OK);
        }
        catch(SearchResultNotFoundException notFoundException) {
            log.error("Result not found");
            throw new SearchResultNotFoundException("Search Result not found");
        }
    }
    @GetMapping("/allga")
    public ResponseEntity<List<GrantingAuthorityResponse>> findAllGA(){
        List<GrantingAuthorityResponse> allGA = null;
        try{
            allGA = accessManagementService.getAllGA();
        }catch (Exception e){
            log.error("Error while fetching all granting authorities");
            throw new SearchResultNotFoundException("Error while fetching all granting authorities");
        }
        return new ResponseEntity<List<GrantingAuthorityResponse>>(allGA, HttpStatus.OK);
    }
    @PutMapping(
            value = "/{awardNumber}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> updateSubsidyAward(@Valid @RequestBody UpdateAwardDetailsRequest awardUpdateRequest,
                                                     @PathVariable("awardNumber") Long awardNumber) {

         log.info("Before calling updateSubsidyAward::{}");
         if (StringUtils.isEmpty(awardNumber) || Objects.isNull(awardUpdateRequest)) {
              throw new InvalidRequestException("Bad Request AwardId is null or requestBody is null");
         }
        return accessManagementService.updateAwardDetailsByAwardId(awardNumber, awardUpdateRequest);
    }

    @GetMapping(
            value = "/searchresults",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SearchSubsidyResultsResponse> retrieveSubsidyAwardDetails(@RequestParam(value = "searchName",
            required = false) String searchName, @RequestParam(value = "status",required = false) String status,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "recordsPerPage", required = false) Integer recordsPerPage) {

        log.info("Before calling retrieveSubsidyAwardDetails::{}");
        //Set Default Page records
        if(recordsPerPage == null) {
            recordsPerPage = 10;
        }

        if(page == null) {
            page = 1;
        }
        SearchSubsidyResultsResponse searchResults = accessManagementService.findMatchingSubsidyMeasureWithAwardDetails(
                searchName, status, page, recordsPerPage);
        return new ResponseEntity<SearchSubsidyResultsResponse>(searchResults, HttpStatus.OK);
    }
}