package com.beis.subsidy.control.accessmanagementservice.controller;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPILoginFeignClient;
import com.beis.subsidy.control.accessmanagementservice.response.AccessTokenResponse;
import com.beis.subsidy.control.accessmanagementservice.response.SearchResults;
import com.beis.subsidy.control.accessmanagementservice.response.UserCountResponse;
import com.beis.subsidy.control.accessmanagementservice.service.UserManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ModelExtensionsKt;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserManagementControllerTest {
    @InjectMocks
    private UserManagementController userManagementController;

    @Mock
    UserManagementService userManagementServiceMock;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    GraphAPILoginFeignClient graphAPILoginFeignClientMock;

    @Mock
    Environment environmentMock;

    HttpHeaders headers = new HttpHeaders();
    String userPrincipleStr;
    UserPrinciple userPrincipleObj;

    @BeforeEach
    public void setUp() throws Exception {
        File upFile = new File("src\\test\\java\\com\\beis\\subsidy\\control\\accessmanagementservice\\data\\user_principle.json");

        ObjectMapper mapper = new ObjectMapper();


        userPrincipleStr = "{\"userName\":\"TEST\",\"password\":\"password123\",\"role\":\"BEIS Administrator\",\"grantingAuthorityGroupId\":\"123\",\"grantingAuthorityGroupName\":\"test\"}";
        userPrincipleObj = mapper.readValue(upFile,UserPrinciple.class);

        List<String> userPrinciple = new ArrayList<>();
        userPrinciple.add(userPrincipleStr);
        headers.put("userPrinciple",userPrinciple);

        userManagementServiceMock = mock(UserManagementService.class);
        MockitoAnnotations.openMocks(this);
    }

    @SneakyThrows
    @Test
    public void testRetrieveAllUserCounts(){
        final HttpStatus expectedHttpStatus = HttpStatus.OK;
        UserCountResponse userCountResponse = new UserCountResponse();

        userCountResponse.setAdminCount(5);
        userCountResponse.setApproverCount(10);
        userCountResponse.setEncoderCount(25);
        userCountResponse.setTotalCount(40);

        when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(UserPrinciple.class))).thenReturn(userPrincipleObj);
        when(environmentMock.getProperty("client-Id")).thenReturn("clientId");
        when(environmentMock.getProperty("client-secret")).thenReturn("clientSecret");
        when(environmentMock.getProperty("graph-api-scope")).thenReturn("graphApiScope");
        when(environmentMock.getProperty("tenant-id")).thenReturn("tenantId");
        when(graphAPILoginFeignClientMock.getAccessIdToken(Mockito.anyString(),Mockito.any())).thenReturn(new AccessTokenResponse("accessToken"));
        when(userManagementServiceMock.getAllUserCounts(Mockito.anyString())).thenReturn(userCountResponse);

        ResponseEntity<?> actual = userManagementController.retrieveAllUserCounts(headers);

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(expectedHttpStatus);
        assertThat(actual.getBody()).isInstanceOf(UserCountResponse.class);
        assert actual.getBody() != null;
        assertThat(((UserCountResponse) actual.getBody()).getAdminCount()).isEqualTo(5);
        assertThat(((UserCountResponse) actual.getBody()).getApproverCount()).isEqualTo(10);
        assertThat(((UserCountResponse) actual.getBody()).getEncoderCount()).isEqualTo(25);
        assertThat(((UserCountResponse) actual.getBody()).getTotalCount()).isEqualTo(40);
    }
}
