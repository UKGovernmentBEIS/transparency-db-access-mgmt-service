package com.beis.subsidy.control.accessmanagementservice.service.impl;

import static com.beis.subsidy.control.accessmanagementservice.utils.JsonFeignResponseUtil.toResponseEntity;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPIFeignClient;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessManagementException;
import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.exception.UnauthorisedAccessException;
import com.beis.subsidy.control.accessmanagementservice.model.AuditLogs;
import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.model.Beneficiary;
import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import com.beis.subsidy.control.accessmanagementservice.model.SubsidyMeasure;
import com.beis.subsidy.control.accessmanagementservice.repository.AuditLogsRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.AwardRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.GrantingAuthorityRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.SubsidyMeasureRepository;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.AuditLogsResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.GrantingAuthorityResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchSubsidyResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.service.AccessManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import feign.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.service.notify.NotificationClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AccessManagementServiceImpl implements AccessManagementService {

    @Autowired
    private GrantingAuthorityRepository grantingAuthorityRepository;

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private SubsidyMeasureRepository subsidyMeasureRepository;

    @Autowired
    private UserManagementServiceImpl userManagementService;

    @Value("${loggingComponentName}")
    private String loggingComponentName;
    
    @Autowired
    private AuditLogsRepository auditLogsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GraphAPIFeignClient graphAPIFeignClient;

    @Autowired
    Environment environment;

    private static final ObjectMapper json = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public SearchResults findBEISAdminDashboardData(UserPrinciple userPrincipleObj) {

        log.info("{}:: Inside findBEISAdminDashboardData {}",loggingComponentName);
        Map<String, Integer> gaUserActivityCount = grantingAuthorityCounts(userPrincipleObj);
        List<Award> awardList = awardRepository.findAll();

        Map<String, Integer> awardUserActivityCount = adminAwardCounts(awardList);
        List<SubsidyMeasure> subsidyMeasuresList = subsidyMeasureRepository.findAll();
        Map<String, Integer> smUserActivityCount = subsidyMeasureCounts(userPrincipleObj, subsidyMeasuresList);
        SearchResults searchResults = null;
        searchResults = new SearchResults(gaUserActivityCount);
        addAwardToSearchResult(awardUserActivityCount,searchResults);
        addSubsidiesToSearchResults(searchResults, smUserActivityCount);
        return searchResults;
    }

    @Override
    public SearchResults findGAAdminDashboardData(UserPrinciple userPrincipleObj) {
        log.info("{}::Inside method of findGAAdminDashboardData", loggingComponentName);
        SearchResults searchResults = new SearchResults();
        Long gaId = getGrantingAuthorityIdByName(userPrincipleObj.getGrantingAuthorityGroupName());
        if(gaId == null || gaId <= 0){
            log.error("{}::Inside if method of findGAAdminDashboardData", loggingComponentName);
            throw new UnauthorisedAccessException("Invalid granting authority name");
        }

        List<Award> allAwardList = awardRepository.findAll(getAwardSpecification(gaId));
        Map<String, Integer> awardUserActionCount = adminAwardCounts(allAwardList);
        addAwardToSearchResult(awardUserActionCount, searchResults);
        log.info("{}::Inside method of findGAAdminDashboardData and allAwardList size {}::", loggingComponentName,
                allAwardList.size());
        List<SubsidyMeasure> allSubObjList = subsidyMeasureRepository.findAll(subsidyMeasureByGrantingAuthority(gaId));
        Map<String, Integer> subObjUserActionCount = subsidyMeasureCounts(userPrincipleObj, allSubObjList);
        addSubsidiesToSearchResults(searchResults, subObjUserActionCount);
        log.info("{}::Inside method of findGAAdminDashboardData and allSubObjList size {}::", loggingComponentName,
                allSubObjList.size());
        return searchResults;
    }
    @Override
    public SearchResults findGAApproverDashboardData(UserPrinciple userPrincipleObj) {
        return findGAAdminDashboardData(userPrincipleObj);
    }

    @Override
    public SearchResults findGAEncoderDashboardData(UserPrinciple userPrincipleObj) {
        return findGAAdminDashboardData(userPrincipleObj);
    }

    @Override
    public List<GrantingAuthorityResponse> getAllGA(){
        List<GrantingAuthorityResponse> allGa = new ArrayList<>();
        List<GrantingAuthority>  authorityList = grantingAuthorityRepository.findAll();
       if (authorityList.isEmpty()) {
             throw new SearchResultNotFoundException("No results found in the response");
        }
        log.info("{}::Inside getAllGA method size {}::", loggingComponentName, authorityList.size());
        authorityList.forEach(ga -> allGa.add(new GrantingAuthorityResponse(ga, null)));
        return allGa;
    }

    @Override
    public List<GrantingAuthorityResponse> getRoleBasedGAs(UserPrinciple userPrincipleObj){
        List<GrantingAuthorityResponse> allGa = new ArrayList<>();
        List<GrantingAuthority> authorityList = null;
        if (AccessManagementConstant.BEIS_ADMIN_ROLE.equals(userPrincipleObj.getRole())) {
            authorityList = grantingAuthorityRepository.findAll();
        } else {
            GrantingAuthority gaObj = grantingAuthorityRepository.findByGrantingAuthorityName
                    (userPrincipleObj.getGrantingAuthorityGroupName());
            authorityList = new ArrayList<>();
            authorityList.add(gaObj);
        }
        if (authorityList.isEmpty()) {
            throw new SearchResultNotFoundException("No results found in the response");
        }
        log.info("{}::Inside getRoleBasedGAs method size {}::", loggingComponentName, authorityList.size());
        authorityList.forEach(ga -> allGa.add(new GrantingAuthorityResponse(ga, null)));
        return allGa;
    }

    @Override
    public ResponseEntity<Object> updateAwardDetailsByAwardId(Long awardId,String approverName, UpdateAwardDetailsRequest awardUpdateRequest
            ,String accessToken) {
        Award award = awardRepository.findByAwardNumber(awardId);
        if (Objects.isNull(award)) {

            throw new SearchResultNotFoundException("Award details not found::" + awardId);
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getStatus())) {
            award.setStatus(awardUpdateRequest.getStatus());
            if ("Published".equals(awardUpdateRequest.getStatus()) &&
                    LocalDate.of(1970,1,1).equals(award.getPublishedAwardDate())) {
                award.setPublishedAwardDate(LocalDate.now());
            }
        }
        award.setLastModifiedTimestamp(LocalDate.now());
        if (!StringUtils.isEmpty(awardUpdateRequest.getSubsidyAmountExact())) {
            award.setSubsidyFullAmountExact(new BigDecimal(awardUpdateRequest.getSubsidyAmountExact()));
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getSubsidyAmountRange())) {
            award.setSubsidyFullAmountRange(awardUpdateRequest.getSubsidyAmountRange());
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getSpendingRegion())) {
            award.setSpendingRegion(awardUpdateRequest.getSpendingRegion());
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getSpendingSector())) {
            award.setSpendingSector(awardUpdateRequest.getSpendingSector());
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getGoodsOrServices())) {
            award.setGoodsServicesFilter(awardUpdateRequest.getGoodsOrServices());
        }
        SubsidyMeasure measure = award.getSubsidyMeasure();

        if (!StringUtils.isEmpty(awardUpdateRequest.getSubsidyControlTitle())) {
            measure.setSubsidyMeasureTitle(awardUpdateRequest.getSubsidyControlTitle().trim());
        }
        award.setSubsidyMeasure(measure);
        Beneficiary beneficiaryDtls = award.getBeneficiary();
        if (!StringUtils.isEmpty(awardUpdateRequest.getNationalId())) {
            beneficiaryDtls.setNationalId(awardUpdateRequest.getNationalId().trim());
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getBeneficiaryName())) {
            beneficiaryDtls.setBeneficiaryName(awardUpdateRequest.getBeneficiaryName().trim());
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getOrgSize())) {
            beneficiaryDtls.setOrgSize(awardUpdateRequest.getOrgSize().trim());
        }
        award.setBeneficiary(beneficiaryDtls);

        GrantingAuthority grantingAuthority = award.getGrantingAuthority();
        if (!StringUtils.isEmpty(awardUpdateRequest.getGrantingAuthorityName())) {
            grantingAuthority.setGrantingAuthorityName(awardUpdateRequest.getGrantingAuthorityName().trim());
            award.setGrantingAuthority(grantingAuthority);
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getStatus()) &&
                ("Rejected".equals(awardUpdateRequest.getStatus().trim()) ||
                 "Deleted".equals(awardUpdateRequest.getStatus().trim())) &&
                !StringUtils.isEmpty(awardUpdateRequest.getReason())) {
            award.setReason(awardUpdateRequest.getReason().trim());
        }
        awardRepository.save(award);

        //notification call START here
        UserDetailsResponse response =  userManagementService.getUserRolesByGrpId(accessToken,grantingAuthority.getAzureGroupId());
        List<UserResponse> users= response.getUserProfiles();

        for (UserResponse userResponse : users) {
              if (!StringUtils.isEmpty(userResponse.getRoleName()) &&
                      userResponse.getRoleName().contains("GrantingAuthorityEncoders") ||
                      userResponse.getRoleName().contains("GrantingAuthorityApprovers")) {
                  try {
                      log.info("{}::email sending to",loggingComponentName);
                      EmailUtils.sendAwardNotificationEmail(userResponse.getMail(),awardUpdateRequest.getStatus(),awardId,
                              approverName,environment);
                  } catch (NotificationClientException e) {
                      log.error("{} :: error in sending feedback mail", loggingComponentName,e);
                  }
              }

        }
        //end Notification
        return ResponseEntity.status(200).build();
    }

    @Override
    public SearchSubsidyResultsResponse findMatchingSubsidyMeasureWithAwardDetails(String searchName,String status,
                             Integer page, Integer recordsPerPage, UserPrinciple userPrinciple,String[] sortBy) {

        Page<Award> pageAwards = null;
        List<Award> awardResults = null;
        List<Award> awards = null;
        Specification<Award> awardSpecifications = getSpecificationAwardDetails(searchName, status);

        List<Sort.Order> orders = getOrderByCondition(sortBy);
        Pageable pagingSortAwards = PageRequest.of(page - 1, recordsPerPage,Sort.by(orders));

        if (AccessManagementConstant.BEIS_ADMIN_ROLE.equals(userPrinciple.getRole().trim())) {
            pageAwards = awardRepository.findAll(awardSpecifications, pagingSortAwards);
            awardResults = pageAwards.getContent();
            awards = awardRepository.findAll(awardSpecifications);

        } else {

            if(!StringUtils.isEmpty(searchName) || !StringUtils.isEmpty(status))  {

                Specification<Award> awardSpecificationsForGaRoles = getSpecificationAwardDetailsByGaRoles(searchName,
                        status,userPrinciple.getGrantingAuthorityGroupName());
                pageAwards = awardRepository.findAll(awardSpecificationsForGaRoles,pagingSortAwards);
                awardResults = pageAwards.getContent();
                awards =  awardRepository.findAll(awardSpecificationsForGaRoles);

            } else {

                Long gaId = getGrantingAuthorityIdByName(userPrinciple.getGrantingAuthorityGroupName());
                if(gaId == null || gaId <= 0){
                    throw new UnauthorisedAccessException("Invalid granting authority name");
                }
                pageAwards = awardRepository.findAll(getAwardSpecification(gaId),pagingSortAwards);
                awardResults = pageAwards.getContent();
                awards =  awardRepository.findAll(getAwardSpecification(gaId));

            }
        }


        SearchSubsidyResultsResponse searchResults = null;

        if (!awardResults.isEmpty()) {
            //List<Award> awards = awardRepository.findAll();
            searchResults = new SearchSubsidyResultsResponse(awardResults, pageAwards.getTotalElements(),
                    pageAwards.getNumber() + 1, pageAwards.getTotalPages(),adminAwardCounts(awards));
        } else {

            throw new SearchResultNotFoundException("AwardResults NotFound");
        }
        return searchResults;
    }

    private void sortAwards(List<Award> awardList) {
        if(awardList != null && awardList.size() > 0)
            awardList.sort((Award award1, Award award2)-> award2.getLastModifiedTimestamp()
                    .compareTo(award1.getLastModifiedTimestamp()));
    }

    private void sortSubsidyMeasure(List<SubsidyMeasure> allSubObjList) {
        if(allSubObjList != null && allSubObjList.size() > 0) {
            allSubObjList.sort((SubsidyMeasure sm1, SubsidyMeasure sm2) -> sm2.getLastModifiedTimestamp()
                    .compareTo(sm1.getLastModifiedTimestamp()));
        }
    }
    private void addAwardToSearchResult(Map<String, Integer> awardUserActivityCount, SearchResults searchResults) {

        searchResults.setAwardUserActionCount(awardUserActivityCount);
    }
    private void addSubsidiesToSearchResults(SearchResults searchResults, Map<String, Integer> subObjUserActionCount) {
          searchResults.setSubsidyMeasureUserActionCount(subObjUserActionCount);
    }
    private Map<String, Integer> subsidyMeasureCounts(UserPrinciple userPrincipleObj, List<SubsidyMeasure> subsidyMeasuresList) {
        int totalSubsidyScheme = subsidyMeasuresList.size();
        int totalActiveScheme = 0;
        int totalInactiveScheme = 0;

        if(subsidyMeasuresList != null && subsidyMeasuresList.size() > 0){
            for(SubsidyMeasure sm : subsidyMeasuresList){
                if(sm.getStatus().equalsIgnoreCase(AccessManagementConstant.SCHEME_ACTIVE)){
                    totalActiveScheme++;
                }
                if(sm.getStatus().equalsIgnoreCase(AccessManagementConstant.SCHEME_INACTIVE)){
                    totalInactiveScheme++;
                }
            }
        }
        Map<String, Integer> smUserActivityCount = new HashMap<>();
        smUserActivityCount.put("totalSubsidyScheme",totalSubsidyScheme);
        smUserActivityCount.put("totalActiveScheme",totalActiveScheme);
        smUserActivityCount.put("totalInactiveScheme",totalInactiveScheme);
        return smUserActivityCount;
    }

    private Map<String, Integer> adminAwardCounts(List<Award> awardList) {
        int totalSubsidyAward = 0;
        int totalAwaitingAward = 0;
        int totalPublishedAward = 0;
        int totalRejectedAward = 0;
        int totalDeleteAward = 0;
        if(awardList != null && awardList.size() >0){
            totalSubsidyAward = awardList.size();
            for(Award award : awardList){
                if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_AWAITING_APPROVAL)){
                    totalAwaitingAward++;
                } else if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_PUBLISHED_STATUS)){
                    totalPublishedAward++;
                } else if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_REJECTED)){
                    totalRejectedAward++;
                } else if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_DELETED)){
                    totalDeleteAward++;
                }
            }
        }
        Map<String, Integer> awardUserActivityCount = new HashMap<>();
        awardUserActivityCount.put("totalSubsidyAward",totalSubsidyAward);
        awardUserActivityCount.put("totalAwaitingAward",totalAwaitingAward);
        awardUserActivityCount.put("totalRejectedAward",totalRejectedAward);
        awardUserActivityCount.put("totalDeleteAward",totalDeleteAward);
        awardUserActivityCount.put("totalPublishedAward",totalPublishedAward);
        return awardUserActivityCount;
    }

    private Map<String, Integer> grantingAuthorityCounts(UserPrinciple userPrincipleObj) {
        List<GrantingAuthority> allGrantingAuthority = grantingAuthorityRepository.findAll();
        int totalGrantingAuthority = allGrantingAuthority.size();
        int totalInactiveGA = 0;
        int totalActiveGA = 0;

        if(allGrantingAuthority != null && allGrantingAuthority.size() > 0){
            totalGrantingAuthority = allGrantingAuthority.size();
            for(GrantingAuthority ga : allGrantingAuthority){
                if( ga.getStatus().equals(AccessManagementConstant.GA_ACTIVE_STATUS)){
                    totalActiveGA++;
                } else if(ga.getStatus().equals(AccessManagementConstant.GA_INACTIVE_STATUS)){
                    totalInactiveGA++;
                }
            }
        }
        Map<String, Integer> gaUserActivityCount = new HashMap<>();
        gaUserActivityCount.put("totalGrantingAuthority",totalGrantingAuthority);
        gaUserActivityCount.put("totalActiveGA",totalActiveGA);
        gaUserActivityCount.put("totalDeactiveGA",totalInactiveGA);
        return gaUserActivityCount;
    }

    private Specification<GrantingAuthority> getGACreatedByUserSpecification(UserPrinciple userPrincipleObj) {
        return Specification.where(
                userPrincipleObj.getUserName() == null || userPrincipleObj.getUserName().isEmpty()
                        ? null : GACreatedBySpecification.grantingAuthorityCreatedBy(userPrincipleObj.getUserName())
        );
    }

    private Specification<SubsidyMeasure> subsidyMeasureByGrantingAuthority(Long gaId) {
        return Specification.where(
                SubsidyMeasureSpecificationUtils.subsidyMeasureByGrantingAuthority(gaId)
        );
    }

    private Specification<Award> getAwardSpecification(Long gaId) {
        return Specification.where(
                AwardSpecificationUtils.awardByGrantingAuthority(gaId));
    }

    private Long getGrantingAuthorityIdByName(String gaName){
        GrantingAuthority gaObj = grantingAuthorityRepository.findByGrantingAuthorityName(gaName);
        if(gaObj != null){
            return gaObj.getGaId();
        }
        return null;
    }

    public Specification<Award>  getSpecificationAwardDetails(String searchName, String status) {

        Specification<Award> awardSpecifications = Specification

                // subsidyMeasureTitle from input parameter
                .where(
                        SearchUtils.checkNullOrEmptyString(searchName)
                                ? null :AwardSpecificationUtils.subsidyMeasureTitle(searchName.trim())
                                .or(searchName != null && searchName.matches("[0-9]+") ?
                                        AwardSpecificationUtils.awardByNumber(Long.valueOf(searchName)):null)
                                .or(SearchUtils.checkNullOrEmptyString(searchName)
                                        ? null :AwardSpecificationUtils.grantingAuthorityName(searchName.trim()))
                                .or(SearchUtils.checkNullOrEmptyString(searchName)
                                        ? null :AwardSpecificationUtils.beneficiaryName(searchName.trim())))
                // status from input parameter
                .and(SearchUtils.checkNullOrEmptyString(status)
                        ? null : AwardSpecificationUtils.awardByStatus(status.trim()));
                // .and (awardNumber != null ? AwardSpecificationUtils.awardByNumber(awardNumber):null);
        return awardSpecifications;
    }

    private Specification<Award> getSpecificationAwardDetailsByGaRoles(String searchName, String status, String gaName) {

        Specification<Award> awardSpecifications = Specification

                // subsidyMeasureTitle from input parameter
                .where(
                        SearchUtils.checkNullOrEmptyString(searchName)
                                ? null :AwardSpecificationUtils.subsidyMeasureTitle(searchName.trim())
                                .or(searchName != null && searchName.matches("[0-9]+") ?
                                        AwardSpecificationUtils.awardByNumber(Long.valueOf(searchName)):null)
                                .or(SearchUtils.checkNullOrEmptyString(searchName)
                                        ? null :AwardSpecificationUtils.grantingAuthorityName(searchName.trim()))
                                .or(SearchUtils.checkNullOrEmptyString(searchName)
                                        ? null :AwardSpecificationUtils.beneficiaryName(searchName.trim())))
                .and(SearchUtils.checkNullOrEmptyString(gaName) ? null :
                        AwardSpecificationUtils.grantingAuthorityName(gaName.trim()))
                .and(SearchUtils.checkNullOrEmptyString(status)
                        ? null : AwardSpecificationUtils.awardByStatus(status.trim()));
        return awardSpecifications;
    }

    private List<Sort.Order> getOrderByCondition(String[] sortBy) {

        List<Sort.Order> orders = new ArrayList<Sort.Order>();

        if (sortBy != null && sortBy.length > 0 && sortBy[0].contains("-")) {
            // will sort more than 2 fields
            // sortOrder="field, direction"
            for (String sortOrder : sortBy) {
                String[] _sort = sortOrder.split("-");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            //Default sort - Legal Granting Date with recent one at top
            orders.add(new Sort.Order(getSortDirection("desc"), "legalGrantingDate"));
        }
        return orders;
    }

    private Sort.Direction getSortDirection(String direction) {
        Sort.Direction sortDir = Sort.Direction.ASC;
        if (direction.equals("desc")) {
            sortDir = Sort.Direction.DESC;
        }
        return sortDir;
    }

    /**
     * Get the group info
     * @param token
     * @param groupId
     * @return
     */

    public UserDetailsResponse getUserRolesByGrpId(String token, String groupId) {
        // Graph API call.
        UserDetailsResponse userDetailsResponse = null;
        Response response = null;
        Object clazz;
        try {
            log.info("{}::before calling toGraph Api is and groupId is {}",loggingComponentName,groupId);

            response = graphAPIFeignClient.getUsersByGroupId("Bearer " + token,groupId);
            if (response.status() == 200) {
                clazz = UserDetailsResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userDetailsResponse
                        = (UserDetailsResponse) responseResponseEntity.getBody();

            } else if (response.status() == 404) {
                throw new SearchResultNotFoundException("Group Id not found");
            } else {
                log.error("get user details by groupId Graph Api is failed ::{}",response.status());
                throw new AccessManagementException(HttpStatus.valueOf(response.status()),
                        "Graph Api failed");
            }

        } catch (FeignException ex) {
            log.error("{}:: get  groupId Graph Api is failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userDetailsResponse;
    }
    
    
    @Override
    public AuditLogsResultsResponse findMatchingAuditLogDetails(UserPrinciple userPrinciple,String searchName, LocalDate searchStartDate,LocalDate searchEndDate,
                             Integer page, Integer recordsPerPage,String[] sortBy) {

    	log.info("{} :: inside findMatchingAuditLogDetails ", loggingComponentName);
        Page<AuditLogs> pageAwards = null;
        List<AuditLogs> auditResults = null;
        Specification<AuditLogs> auditSpecifications;
        List<Sort.Order> orders = getOrderByConditionAudits(sortBy);
        Pageable pagingSortAwards = PageRequest.of(page - 1, recordsPerPage,Sort.by(orders));

        if (AccessManagementConstant.BEIS_ADMIN_ROLE.equals(userPrinciple.getRole().trim())) {
            auditSpecifications = getSpecificationAuditDetails(searchName,searchStartDate,searchEndDate);
            pageAwards = auditLogsRepository.findAll(auditSpecifications, pagingSortAwards);

        } else {

            if (!StringUtils.isEmpty(searchName) || searchStartDate != null
                    || searchEndDate != null) {

                log.info("{} :: inside else if for GA Admin user ", loggingComponentName);
                auditSpecifications = getGAAdminSpecificationAuditDetails(searchName,searchStartDate,searchEndDate,
                        userPrinciple.getGrantingAuthorityGroupName());
                pageAwards = auditLogsRepository.findAll(auditSpecifications,pagingSortAwards);

            } else {

                log.info("{} :: inside else of else GA Admin user", loggingComponentName);
                pageAwards = auditLogsRepository.findByGaName(userPrinciple.getGrantingAuthorityGroupName().trim(),
                        pagingSortAwards);
            }

        }

        auditResults = pageAwards.getContent();
        AuditLogsResultsResponse searchResults = null;

        if (!auditResults.isEmpty()) {

        	log.info("{} :: auditResults were not empty",loggingComponentName);
            searchResults = new AuditLogsResultsResponse(auditResults, pageAwards.getTotalElements(),
                    pageAwards.getNumber() + 1, pageAwards.getTotalPages());
        } else {

            throw new SearchResultNotFoundException("Audit Logs NotFound");
        }
        return searchResults;
    }

    public Specification<AuditLogs>  getSpecificationAuditDetails(String searchName,LocalDate searchStartDate,LocalDate searchEndDate) {

        Specification<AuditLogs> auditSpecifications = Specification

        .where(
                SearchUtils.checkNullOrEmptyString(searchName)
                        ? null :AwardSpecificationUtils.auditUserLikeSearch(searchName.trim())
                        .or(SearchUtils.checkNullOrEmptyString(searchName)
                                ? null :AwardSpecificationUtils.auditGrantingAuthority(searchName.trim())))
        .and((searchStartDate==null || searchEndDate==null)
                                ? null :AwardSpecificationUtils.auditLogRange(searchStartDate,searchEndDate));
        return auditSpecifications;

    }


    public Specification<AuditLogs>  getGAAdminSpecificationAuditDetails(String searchName,LocalDate searchStartDate,
                                                                         LocalDate searchEndDate,String gaName) {

        Specification<AuditLogs> auditSpecifications = Specification

                .where(
                        SearchUtils.checkNullOrEmptyString(searchName)
                                ? null :AwardSpecificationUtils.auditUserEqualSearch(searchName.trim()))
                .and((searchStartDate==null || searchEndDate==null)
                        ? null :AwardSpecificationUtils.auditLogRange(searchStartDate,searchEndDate))
                .and(SearchUtils.checkNullOrEmptyString(gaName)
                        ? null :AwardSpecificationUtils.searchByGrantingAuthorityName(gaName.trim()));
        return auditSpecifications;

    }

    private List<Sort.Order> getOrderByConditionAudits(String[] sortBy) {

        List<Sort.Order> orders = new ArrayList<Sort.Order>();

        if (sortBy != null && sortBy.length > 0 && sortBy[0].contains(",")) {
            //  will sort more than 2 fields
            // sortOrder="field, direction"
            for (String sortOrder : sortBy) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            //Default sort - Legal Granting Date with recent one at top
            orders.add(new Sort.Order(getSortDirection("desc"), "createdTimestamp"));
        }
        return orders;
    }


}
