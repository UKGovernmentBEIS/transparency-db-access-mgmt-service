package com.beis.subsidy.control.accessmanagementservice.controller;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPILoginFeignClient;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessManagementException;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessTokenException;
import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.exception.UnauthorisedAccessException;
import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.AccessTokenResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;
import com.beis.subsidy.control.accessmanagementservice.service.UserManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.AccessManagementConstant;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(path = "/usermanagement")
@RestController
@Slf4j
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GraphAPILoginFeignClient graphAPILoginFeignClient;

    static final String BEARER = "Bearer ";

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    Environment environment;

    @GetMapping(
            value = "/users",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> retrieveAllUserDetails(@RequestHeader("userPrinciple") HttpHeaders userPrinciple) {

       log.info("Before calling retrieveAllUserDetails");
       getRoleFromUserPrincipleObject(userPrinciple);
       String access_token = getBearerToken();
       log.info("access_token in retrieveAllUserDetails");
       UserDetailsResponse response =  userManagementService.getAllUsers(access_token);
       return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/adduser",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> addUser(@RequestHeader("UserPrinciple") HttpHeaders userPrinciple,
                                          @RequestBody AddUserRequest request) {

        log.info("{}::Before calling addUser", loggingComponentName);
        getRoleFromUserPrincipleObject(userPrinciple);
        String access_token = getBearerToken();
        UserResponse response =  userManagementService.addUser(access_token,request);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping(
            value = "/deleteuser/{userId}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> deleteUser(@RequestHeader("UserPrinciple") HttpHeaders userPrinciple,
                                          @PathVariable("userId") String userId) {

        log.info("{}::Before calling deleteUser", loggingComponentName);
        getRoleFromUserPrincipleObject(userPrinciple);
        if (StringUtils.isEmpty(userId)) {
            log.error("{}:: userId is empty:: {}",loggingComponentName, userId);
            throw new InvalidRequestException("userId is null or empty");

        }
        String access_token = getBearerToken();
        int response =  userManagementService.deleteUser(access_token,userId);
        return ResponseEntity.status(204).body(response);
    }

    @GetMapping(
            value = "/groups/{groupId}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> retrieveUserDetailsByGroupId(@RequestHeader("userPrinciple") HttpHeaders userPrinciple,
                                                               @PathVariable("groupId") String groupId) {

        log.info("{}:: Before calling retrieveUserDetailsByGroupId",loggingComponentName);
        getRoleFromUserPrincipleObject(userPrinciple);
        String access_token = getBearerToken();
        log.info("{}:: After access_token in retrieveUserDetailsByGroupId",loggingComponentName);
        UserDetailsResponse response =  userManagementService.getUserRolesByGrpId(access_token,groupId);
        return ResponseEntity.status(200).body(response);
    }

    public String getBearerToken() throws AccessTokenException {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", environment.getProperty("client-Id"));
        map.add("client_secret",environment.getProperty("client-secret"));
        map.add("scope", environment.getProperty("graph-api-scope"));

        AccessTokenResponse openIdTokenResponse = graphAPILoginFeignClient
                .getAccessIdToken(environment.getProperty("tenant-id"),map);
        if (openIdTokenResponse == null) {
            throw new AccessTokenException(HttpStatus.valueOf(500),
                    "Graph Api Service Failed while bearer token generate");
        }
        return openIdTokenResponse.getAccessToken();
    }

    public  void getRoleFromUserPrincipleObject(HttpHeaders userPrinciple) {
        UserPrinciple userPrincipleObj = null;
        String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
        try {
            userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
            if (!Arrays.asList(AccessManagementConstant.ROLES).contains(userPrincipleObj.getRole())) {
                throw new UnauthorisedAccessException("You are not authorised to view Admin Dashboard");
            }
        } catch(JsonProcessingException exception){
            throw new AccessManagementException(HttpStatus.BAD_REQUEST,"JSON parsing Exception");
        }
    }

}