package com.beis.subsidy.control.accessmanagementservice.controller;

import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateAwardDetailsRequest;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.SearchSubsidyResultsResponse;
import com.beis.subsidy.control.accessmanagementservice.service.AccessManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccessManagementControllerTest {
    @InjectMocks
    private AccessManagementController accessManagementController;

    @Mock
    AccessManagementService accessManagementServiceMock;

    @Mock
    ObjectMapper objectMapper;

    HttpHeaders headers = new HttpHeaders();


    @BeforeEach
    public void setUp() throws Exception {
        accessManagementServiceMock = mock(AccessManagementService.class);
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void test_getHealth() {
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        ResponseEntity<?> actual = accessManagementController.getHealth();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        assertThat(actual.getBody().toString().equals("Successful health check - Access Management API"));
    }
    @Test
    public void test_findBEISAdminDashboardData(){
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        try {
            File file = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\beis_admin_dashboard_data.json");
            File upFile = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\user_principle.json");

            ObjectMapper mapper = new ObjectMapper();
            SearchResults searchResults = mapper.readValue(file, SearchResults.class);
            HttpHeaders headers = new HttpHeaders();
            String userPrincipleStr = "{\"userName\":\"TEST\",\"password\":\"password123\",\"role\":\"BEIS Administrator\",\"grantingAuthorityGroupId\":\"123\",\"grantingAuthorityGroupName\":\"test\"}";
            UserPrinciple userPrincipleObj = mapper.readValue(upFile,UserPrinciple.class);

            List<String> userPrinciple = new ArrayList<>();
            userPrinciple.add(userPrincipleStr);
            headers.put("userPrinciple",userPrinciple);

            when(accessManagementServiceMock.findBEISAdminDashboardData(Mockito.any())).thenReturn(searchResults);
            when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(UserPrinciple.class))).thenReturn(userPrincipleObj);
            ResponseEntity<SearchResults> actual = accessManagementController.findBEISAdminDashboardData(headers);

            assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
            assertThat(actual.getBody().getGrantingAuthorityResponse().size()).isLessThanOrEqualTo(5);
            assertThat(actual.getBody().getAwardResponse().size()).isLessThanOrEqualTo(5);
            assertThat(actual.getBody().getSubsidyMeasureResponse().size()).isLessThanOrEqualTo(5);
        } catch (IOException e) {
        }
    }

    @Test
    public void test_findGaAdminDashboardData(){
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        try {
            File file = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\beis_admin_dashboard_data.json");
            File upFile = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\user_principle.json");

            ObjectMapper mapper = new ObjectMapper();
            SearchResults searchResults = mapper.readValue(file, SearchResults.class);
            HttpHeaders headers = new HttpHeaders();
            String userPrincipleStr = "{\"userName\":\"TEST\",\"password\":\"password123\",\"role\":\"Granting Authority Administrator\",\"grantingAuthorityGroupId\":\"123\",\"grantingAuthorityGroupName\":\"HMRC\"}";
            UserPrinciple userPrincipleObj = mapper.readValue(upFile,UserPrinciple.class);
            userPrincipleObj.setRole("Granting Authority Administrator");
            userPrincipleObj.setGrantingAuthorityGroupName("HMRC");

            List<String> userPrinciple = new ArrayList<>();
            userPrinciple.add(userPrincipleStr);
            headers.put("userPrinciple",userPrinciple);

            when(accessManagementServiceMock.findBEISAdminDashboardData(Mockito.any())).thenReturn(searchResults);
            when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(UserPrinciple.class))).thenReturn(userPrincipleObj);
            ResponseEntity<SearchResults> actual = accessManagementController.findGAAdminDashboardData(headers);

            assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        } catch (IOException e) {
        }
    }
 }
