package com.beis.subsidy.control.accessmanagementservice.service.impl;

import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.exception.UnauthorisedAccessException;
import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.model.Beneficiary;
import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import com.beis.subsidy.control.accessmanagementservice.model.SubsidyMeasure;
import com.beis.subsidy.control.accessmanagementservice.repository.AwardRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.GrantingAuthorityRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.SubsidyMeasureRepository;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.*;
import com.beis.subsidy.control.accessmanagementservice.service.AccessManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccessManagementServiceImpl implements AccessManagementService {
    @Autowired
    private GrantingAuthorityRepository grantingAuthorityRepository;

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private SubsidyMeasureRepository subsidyMeasureRepository;

    @Override
    public SearchResults findBEISAdminDashboardData(UserPrinciple userPrincipleObj) {
        Pageable pagingSortAwards = PageRequest.of(0,AccessManagementConstant.TOP_GA_TO_DISPLAY, Sort.by("lastModifiedTimestamp").descending());
        List<GrantingAuthority> top5GA = grantingAuthorityRepository
                .findAll(pagingSortAwards).getContent();
        Map<String, Integer> gaUserActivityCount = grantingAuthorityCounts(userPrincipleObj);

        List<Award> awardList = awardRepository.findAll();
        Map<String, Integer> awardUserActivityCount = adminAwardCounts(userPrincipleObj, awardList);
        List<Award> top5Awards = new ArrayList<>();
        sortAwards(awardList);
        if(awardList != null && awardList.size() > AccessManagementConstant.TOP_AWARD_TO_DISPLAY){
            awardList.stream().limit(AccessManagementConstant.TOP_AWARD_TO_DISPLAY).forEach(award -> {
                top5Awards.add(award);
            });
        }else {
            top5Awards.addAll(awardList);
        }

        List<SubsidyMeasure> subsidyMeasuresList = subsidyMeasureRepository.findAll();
        Map<String, Integer> smUserActivityCount = subsidyMeasureCounts(userPrincipleObj, subsidyMeasuresList);
        List<SubsidyMeasure> top5subsidyMeasures = new ArrayList<>();
        sortSubsidyMeasure(subsidyMeasuresList);
        if(subsidyMeasuresList != null && subsidyMeasuresList.size() > AccessManagementConstant.TOP_SM_TO_DISPLAY){
            subsidyMeasuresList.stream().limit(AccessManagementConstant.TOP_SM_TO_DISPLAY).forEach(subsidyMeasure -> {
                top5subsidyMeasures.add(subsidyMeasure);
            });
        } else {
            top5subsidyMeasures.addAll(subsidyMeasuresList);
        }
        SearchResults searchResults = null;
        if (top5GA.size() > 0) {
            searchResults = new SearchResults(top5GA,gaUserActivityCount);
            addAwardToSearchResult(top5Awards, awardUserActivityCount, searchResults);
            addSubsidiesToSearchResults(searchResults, top5subsidyMeasures, smUserActivityCount);
        } else {
            throw new SearchResultNotFoundException("Granting Authority Results NotFound");
        }
        return searchResults;
    }

    @Override
    public SearchResults findGAAdminDashboardData(UserPrinciple userPrincipleObj) {
        SearchResults searchResults = new SearchResults();
        Long gaId = getGrantingAuthorityIdByName(userPrincipleObj.getGrantingAuthorityGroupName());
        if(gaId == null || gaId <= 0){
            throw new UnauthorisedAccessException("Invalid granting authority name");
        }

        List<Award> top5Awards = new ArrayList<>();
        List<Award> allAwardList = awardRepository.findAll(getAwardSpecification(gaId));
        Map<String, Integer> awardUserActionCount = adminAwardCounts(userPrincipleObj, allAwardList);
        sortAwards(allAwardList);
        if(allAwardList != null && allAwardList.size() > AccessManagementConstant.TOP_AWARD_TO_DISPLAY){
            allAwardList.stream().limit(AccessManagementConstant.TOP_AWARD_TO_DISPLAY).forEach(award -> {
                top5Awards.add(award);
            });
        } else {
            top5Awards.addAll(allAwardList);
        }
        addAwardToSearchResult(top5Awards, awardUserActionCount, searchResults);

        List<SubsidyMeasure> allSubObjList = subsidyMeasureRepository.findAll(subsidyMeasureByGrantingAuthority(gaId));
        Map<String, Integer> subObjUserActionCount = subsidyMeasureCounts(userPrincipleObj, allSubObjList);
        List<SubsidyMeasure> top5subsidyMeasure = new ArrayList<>();
        sortSubsidyMeasure(allSubObjList);
        if(allSubObjList != null && allSubObjList.size() > AccessManagementConstant.TOP_SM_TO_DISPLAY) {
           allSubObjList.stream().limit(AccessManagementConstant.TOP_SM_TO_DISPLAY).forEach(sm -> {
               top5subsidyMeasure.add(sm);
           });
        } else {
            top5subsidyMeasure.addAll(allSubObjList);
        }
        addSubsidiesToSearchResults(searchResults, top5subsidyMeasure, subObjUserActionCount);
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
        List<GrantingAuthority> authorityList = grantingAuthorityRepository.findAll();
        authorityList.forEach(ga -> allGa.add(new GrantingAuthorityResponse(ga, null)));
        return allGa;
    }
    @Override
    public ResponseEntity<Object> updateAwardDetailsByAwardId(Long awardId, UpdateAwardDetailsRequest awardUpdateRequest) {
        Award award = awardRepository.findByAwardNumber(awardId);
        if (Objects.isNull(award)) {

            throw new SearchResultNotFoundException("Award details not found::" + awardId);
        }
        if (!StringUtils.isEmpty(awardUpdateRequest.getStatus())) {
            award.setStatus(awardUpdateRequest.getStatus());
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
                "Rejected".equals(awardUpdateRequest.getStatus().trim()) &&
                !StringUtils.isEmpty(awardUpdateRequest.getReason())) {
            award.setReason(awardUpdateRequest.getReason().trim());
        }
      awardRepository.save(award);
      return ResponseEntity.status(200).build();
    }

    @Override
    public SearchSubsidyResultsResponse findMatchingSubsidyMeasureWithAwardDetails(String searchName, String status, Integer page, Integer recordsPerPage) {
        Specification<Award> awardSpecifications = getSpecificationAwardDetails(searchName, status);

        Pageable pagingSortAwards = PageRequest.of(page - 1, recordsPerPage);

        Page<Award> pageAwards = awardRepository.findAll(awardSpecifications, pagingSortAwards);

        List<Award> awardResults = pageAwards.getContent();

        SearchSubsidyResultsResponse searchResults = null;

        if (!awardResults.isEmpty()) {

            searchResults = new SearchSubsidyResultsResponse(awardResults, pageAwards.getTotalElements(),
                    pageAwards.getNumber() + 1, pageAwards.getTotalPages());
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
    private void addAwardToSearchResult(List<Award> top5Awards, Map<String, Integer> awardUserActivityCount, SearchResults searchResults) {
        if(top5Awards.size() > 0){
            searchResults.setAwardResponse(top5Awards
                    .stream()
                    .map(award -> new AwardResponse(award))
                    .collect(Collectors.toList()));
        }
        searchResults.setAwardUserActionCount(awardUserActivityCount);
    }
    private void addSubsidiesToSearchResults(SearchResults searchResults, List<SubsidyMeasure> top5subsidyMeasure, Map<String, Integer> subObjUserActionCount) {
        if(top5subsidyMeasure.size() > 0){
            searchResults.setSubsidyMeasureResponse(top5subsidyMeasure
                    .stream()
                    .map(sm -> new SubsidyMeasureResponse(sm))
                    .collect(Collectors.toList()));
        }
        searchResults.setSubsidyMeasureUserActionCount(subObjUserActionCount);
    }
    private Map<String, Integer> subsidyMeasureCounts(UserPrinciple userPrincipleObj, List<SubsidyMeasure> subsidyMeasuresList) {
        int totalSubsidyMeasures = subsidyMeasuresList.size();
        int totalAwaitingSubsidyMeasures = 0;
        int totalPublishedSubsidyMeasures = 0;
        int totalDraftSubsidyMeasures = 0;
        int totalDeletedSubsidyMeasures = 0;

        if(subsidyMeasuresList != null && subsidyMeasuresList.size() > 0){
            for(SubsidyMeasure sm : subsidyMeasuresList){
                if(sm.getStatus().equalsIgnoreCase(AccessManagementConstant.SM_AWAITING_APPROVAL)){
                    totalAwaitingSubsidyMeasures++;
                }
                if(sm.getStatus().equalsIgnoreCase(AccessManagementConstant.SM_PUBLISHED_STATUS)){
                    totalPublishedSubsidyMeasures++;
                }
                if(sm.getStatus().equalsIgnoreCase(AccessManagementConstant.SM_DRAFT)){
                    totalDraftSubsidyMeasures++;
                }
                if(sm.getStatus().equalsIgnoreCase(AccessManagementConstant.SM_DELETED)){
                    totalDeletedSubsidyMeasures++;
                }
                /*if(sm.getCreatedBy().equals(userPrincipleObj.getUserName())){
                    totalUserSubsidyMeasures++;
                }*/
            }
        }
        Map<String, Integer> smUserActivityCount = new HashMap<>();
        smUserActivityCount.put("totalSubsidyMeasures",totalSubsidyMeasures);
        smUserActivityCount.put("totalAwaitingSubsidyMeasures",totalAwaitingSubsidyMeasures);
        smUserActivityCount.put("totalPublishedSubsidyMeasures",totalPublishedSubsidyMeasures);
        smUserActivityCount.put("totalDraftSubsidyMeasures",totalDraftSubsidyMeasures);
        smUserActivityCount.put("totalDeletedSubsidyMeasures",totalDeletedSubsidyMeasures);
        return smUserActivityCount;
    }
    private Map<String, Integer> adminAwardCounts(UserPrinciple userPrincipleObj, List<Award> awardList) {
        int totalSubsidyAward = 0;
        int totalAwaitingAward = 0;
        int totalPublishedAward = 0;
        int totalDraftAward = 0;
        int totalDeletedAward = 0;
        if(awardList != null && awardList.size() >0){
            totalSubsidyAward = awardList.size();
            for(Award award : awardList){
                if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_AWAITING_APPROVAL)){
                    totalAwaitingAward++;
                }
                if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_PUBLISHED_STATUS)){
                    totalPublishedAward++;
                }
                if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_DRAFT)){
                    totalDraftAward++;
                }
                if(award.getStatus().equalsIgnoreCase(AccessManagementConstant.AWARD_DELETED)){
                    totalDeletedAward++;
                }
                /*if(award.getCreatedBy().equals(userPrincipleObj.getUserName())){
                    totalUserSubsidyAward++;
                }*/
            }
        }
        Map<String, Integer> awardUserActivityCount = new HashMap<>();
        awardUserActivityCount.put("totalSubsidyAward",totalSubsidyAward);
        awardUserActivityCount.put("totalAwaitingAward",totalAwaitingAward);
        awardUserActivityCount.put("totalDraftAward",totalDraftAward);
        awardUserActivityCount.put("totalDeletedAward",totalDeletedAward);
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
                /*if(! ga.getStatus().equals(AccessManagementConstant.GA_PUBLISHED_STATUS)){
                    totalNotPublishedGA++;
                }
                if(ga.getCreatedBy().equals(userPrincipleObj.getUserName())){
                    totalGAPublishedByUser++;
                }*/
            }
        }
        Map<String, Integer> gaUserActivityCount = new HashMap<>();
        gaUserActivityCount.put("totalGrantingAuthority",totalGrantingAuthority);
        gaUserActivityCount.put("totalActiveGA",totalActiveGA);
        gaUserActivityCount.put("totalDeactiveGA",totalInactiveGA);
        //gaUserActivityCount.put("totalNotPublishedGA",totalNotPublishedGA);
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
                    .or(SearchUtils.checkNullOrEmptyString(searchName)
                        ? null :AwardSpecificationUtils.subsidyNumber(searchName.trim()))
                    .or(SearchUtils.checkNullOrEmptyString(searchName)
                       ? null :AwardSpecificationUtils.grantingAuthorityName(searchName.trim()))
                    .or(SearchUtils.checkNullOrEmptyString(searchName)
                        ? null :AwardSpecificationUtils.beneficiaryName(searchName.trim())))
                 // status from input parameter
                .and(SearchUtils.checkNullOrEmptyString(status)
                     ? null : AwardSpecificationUtils.awardByStatus(status.trim()));
        return awardSpecifications;
    }
}
