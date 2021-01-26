package com.beis.subsidy.control.accessmanagementservice.service;

import com.beis.subsidy.control.accessmanagementservice.model.*;
import com.beis.subsidy.control.accessmanagementservice.repository.AwardRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.GrantingAuthorityRepository;
import com.beis.subsidy.control.accessmanagementservice.repository.SubsidyMeasureRepository;
import com.beis.subsidy.control.accessmanagementservice.response.AwardResponse;
import com.beis.subsidy.control.accessmanagementservice.response.GrantingAuthorityResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.SubsidyMeasureResponse;
import com.beis.subsidy.control.accessmanagementservice.service.impl.AccessManagementServiceImpl;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AccessManagementServiceImplTest {
    @InjectMocks
    private AccessManagementServiceImpl accessManagementService;

    @Mock
    private GrantingAuthorityRepository grantingAuthorityRepository;

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private SubsidyMeasureRepository subsidyMeasureRepository;

    Award award;
    List<Award> awards = new ArrayList<>();
    List<SubsidyMeasure> subsidyMeasures = new ArrayList<>();
    List<GrantingAuthority> ga = new ArrayList<>();

    @BeforeEach
    public void setUp() throws Exception {
        award = new Award();
        award.setAwardNumber(1l);
        award.setApprovedBy("system");
        award.setCreatedBy("system");
        award.setCreatedTimestamp(LocalDate.now());
        award.setGoodsServicesFilter("serviceFilter");
        award.setLegalGrantingDate(LocalDate.now());
        award.setSubsidyFullAmountRange("5000");
        award.setSubsidyInstrument("subsidyInstrument");
        award.setSubsidyObjective("subsidyObj");
        award.setSubsidyFullAmountExact(new BigDecimal(100000.0));
        award.setSpendingSector("spendingSector");
        award.setLegalGrantingDate(LocalDate.now());
        award.setStatus("status");
        award.setLastModifiedTimestamp(LocalDate.now());
        award.setPublishedAwardDate(LocalDate.now());
        //beneficiary details
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setBeneficiaryId(1l);
        beneficiary.setNationalId("nationalId");
        beneficiary.setBeneficiaryName("bName");
        beneficiary.setOrgSize("1");
        award.setBeneficiary(beneficiary);

        //SubsidyMeasure
        SubsidyMeasure subsidyMeasure = new  SubsidyMeasure();
        subsidyMeasure.setScNumber("SC10001");
        subsidyMeasure.setSubsidyMeasureTitle("title");
        subsidyMeasure.setAdhoc(false);
        subsidyMeasure.setDuration(new BigInteger("200"));
        subsidyMeasure.setStatus("DEFAULT");
        subsidyMeasure.setGaSubsidyWebLink("www.BEIS.com");
        subsidyMeasure.setStatus("DEFAULT");
        subsidyMeasure.setStatus("DEFAULT");
        subsidyMeasure.setStatus("DEFAULT");
        subsidyMeasure.setStartDate(LocalDate.now());
        subsidyMeasure.setEndDate(LocalDate.now());
        subsidyMeasure.setBudget("budget");
        subsidyMeasure.setPublishedMeasureDate(LocalDate.now());
        subsidyMeasure.setCreatedBy("SYSTEM");
        subsidyMeasure.setApprovedBy("SYSTEM");
        subsidyMeasure.setLastModifiedTimestamp(LocalDate.now());
        // Legal Basis text
        LegalBasis legalBasis = new LegalBasis();
        legalBasis.setLegalBasisText("legal text");
        subsidyMeasure.setLegalBases(legalBasis);



        //GrantingAuthority details
        GrantingAuthority grantingAuthority = new GrantingAuthority();
        grantingAuthority.setGrantingAuthorityName("ganame");
        grantingAuthority.setStatus("DRAFT");
        grantingAuthority.setCreatedBy("TEST");
        grantingAuthority.setGaId(10L);
        grantingAuthority.setLastModifiedTimestamp(LocalDate.now());
        award.setGrantingAuthority(grantingAuthority);
        awards.add(award);
        ga.add(grantingAuthority);
        subsidyMeasure.setGrantingAuthority(grantingAuthority);
        subsidyMeasures.add(subsidyMeasure);
        award.setSubsidyMeasure(subsidyMeasure);

        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void test_findBEISAdminDashboardData() throws Exception{
        File file = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\beis_admin_dashboard_data.json");
        ObjectMapper mapper = new ObjectMapper();
        //SearchResults searchResults = mapper.readValue(file, SearchResults.class);
        //SearchResults searchResults = new SearchResults();
        UserPrinciple userPrincipleObj = getUserPrinciple();

        Page<Award> awardPage = (Page<Award>) mock(Page.class);
        Page<GrantingAuthority> gaListPage = (Page<GrantingAuthority>) mock(Page.class);
        Page<SubsidyMeasure> awardSubMes = (Page<SubsidyMeasure>) mock(Page.class);
        Pageable pagingSortAwards = PageRequest.of(0,5, Sort.by("lastModifiedTimestamp").descending());

        //List<Award> awardList = getAwardList(awards);
        //List<GrantingAuthority> gaList = getGrantingAuthorityList(searchResults.getGrantingAuthorityResponse());
        //List<SubsidyMeasure> smList = getSubsidyMeasure(searchResults.getSubsidyMeasureResponse());

        when(grantingAuthorityRepository.findAll(Mockito.any(Pageable.class))).thenReturn(gaListPage);
        when(grantingAuthorityRepository.findAll(pagingSortAwards).getContent()).thenReturn(ga);
        when(grantingAuthorityRepository.findAll()).thenReturn(ga);

        when(awardRepository.findAll(Mockito.any(Pageable.class))).thenReturn(awardPage);
        when(awardRepository.findAll()).thenReturn(awards);

        when(subsidyMeasureRepository.findAll(Mockito.any(Pageable.class))).thenReturn(awardSubMes);
        when(subsidyMeasureRepository.findAll()).thenReturn(subsidyMeasures);

        SearchResults beisAdminDashboardData = accessManagementService.findBEISAdminDashboardData(userPrincipleObj);
        assertThat(beisAdminDashboardData).isNotNull();
    }

    @Test
    public void test_findGaAdminDashboardData() throws Exception{
        File file = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\beis_admin_dashboard_data.json");
        //ObjectMapper mapper = new ObjectMapper();
        //SearchResults searchResults = mapper.readValue(file, SearchResults.class);
        UserPrinciple userPrincipleObj = getUserPrinciple();
        userPrincipleObj.setRole("Granting Authority Administrator");
        userPrincipleObj.setGrantingAuthorityGroupName("HMRC");
        //Page<Award> awardPage = (Page<Award>) mock(Page.class);
        Page<GrantingAuthority> gaListPage = (Page<GrantingAuthority>) mock(Page.class);
        //Page<SubsidyMeasure> awardSubMes = (Page<SubsidyMeasure>) mock(Page.class);
        //Pageable pagingSortAwards = PageRequest.of(0,5, Sort.by("lastModifiedTimestamp").descending());

        //List<Award> awardList = getAwardList(searchResults.getAwardResponse());
        //List<GrantingAuthority> gaList = getGrantingAuthorityList(searchResults.getGrantingAuthorityResponse());
        //List<SubsidyMeasure> smList = getSubsidyMeasure(searchResults.getSubsidyMeasureResponse());

        when(grantingAuthorityRepository.findAll(Mockito.any(Pageable.class))).thenReturn(gaListPage);
        when(grantingAuthorityRepository.findAll()).thenReturn(ga);
        when(awardRepository.findAll()).thenReturn(awards);
        when(subsidyMeasureRepository.findAll()).thenReturn(subsidyMeasures);
        when(grantingAuthorityRepository.findByGrantingAuthorityName(Mockito.anyString())).thenReturn(ga.get(0));

        SearchResults beisAdminDashboardData = accessManagementService.findGAAdminDashboardData(userPrincipleObj);
        assertThat(beisAdminDashboardData).isNotNull();
    }

    private UserPrinciple getUserPrinciple(){
        UserPrinciple user = new UserPrinciple();
        user.setGrantingAuthorityGroupId(11);
        user.setUserName("test");
        user.setGrantingAuthorityGroupName("test_group");
        user.setPassword("xxx");
        user.setRole("admin");
        return user;
    }
    private List<Award> getAwardList(List<AwardResponse> awardResponses){
        List<Award> awardList = new ArrayList<>();
        Award award = null;
        for(AwardResponse ar : awardResponses){
            award = new Award();
            award.setAwardNumber(ar.getAwardNumber());
            award.setSubsidyFullAmountExact(new BigDecimal(12));
            award.setStatus(ar.getStatus());
            award.setGrantingAuthority(new GrantingAuthority());
            award.getGrantingAuthority().setGrantingAuthorityName(ar.getGaName());
            award.setCreatedBy("TEST");
            award.setLastModifiedTimestamp(LocalDate.now());
            awardList.add(award);
        }
        return awardList;
    }
    private List<SubsidyMeasure> getSubsidyMeasure(List<SubsidyMeasureResponse> subsidyMeasureResponses){
        List<SubsidyMeasure> subsidyMeasureList =  new ArrayList<>();
        SubsidyMeasure subsidyMeasure = null;
        for(SubsidyMeasureResponse smr : subsidyMeasureResponses){
            subsidyMeasure = new SubsidyMeasure();
            subsidyMeasure.setScNumber(smr.getScNumber());
            subsidyMeasure.setSubsidyMeasureTitle(smr.getSubsidyMeasureTitle());
            subsidyMeasure.setStartDate(LocalDate.now());
            subsidyMeasure.setEndDate(LocalDate.now());
            subsidyMeasure.setDuration(new BigInteger(smr.getDuration()));
            subsidyMeasure.setBudget(smr.getBudget());
            subsidyMeasure.setGrantingAuthority(new GrantingAuthority());
            subsidyMeasure.getGrantingAuthority().setGrantingAuthorityName(smr.getGaName());
            subsidyMeasure.setCreatedBy("TEST");
            subsidyMeasure.setLastModifiedTimestamp(LocalDate.now());
            subsidyMeasureList.add(subsidyMeasure);
        }
        return subsidyMeasureList;
    }
    private List<GrantingAuthority> getGrantingAuthorityList(List<GrantingAuthorityResponse> grantingAuthorityResponses){
        List<GrantingAuthority> grantingAuthorityList = new ArrayList<>();
        GrantingAuthority grantingAuthority = null;
        for(GrantingAuthorityResponse ga : grantingAuthorityResponses){
            grantingAuthority = new GrantingAuthority();
            grantingAuthority.setGaId(ga.getGaId());
            grantingAuthority.setGrantingAuthorityName(ga.getGaName());
            //grantingAuthority.setLegalBasis(ga.getLegalBasis());
            grantingAuthority.setStatus(ga.getStatus());
            grantingAuthority.setLastModifiedTimestamp(LocalDate.now());
            grantingAuthority.setCreatedBy("TEST");
            grantingAuthority.setLastModifiedTimestamp(LocalDate.now());
            grantingAuthorityList.add(grantingAuthority);
        }
        return grantingAuthorityList;
    }
}
