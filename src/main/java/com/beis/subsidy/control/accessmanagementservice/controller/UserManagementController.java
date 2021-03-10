package com.beis.subsidy.control.accessmanagementservice.controller;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPILoginFeignClient;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessManagementException;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessTokenException;
import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.model.AuditLogs;
import com.beis.subsidy.control.accessmanagementservice.repository.AuditLogsRepository;
import com.beis.subsidy.control.accessmanagementservice.request.*;
import com.beis.subsidy.control.accessmanagementservice.response.AccessTokenResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRoleResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRolesResponse;
import com.beis.subsidy.control.accessmanagementservice.service.UserManagementService;
import com.beis.subsidy.control.accessmanagementservice.utils.EmailUtils;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.beis.subsidy.control.accessmanagementservice.utils.UserPrinciple;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.service.notify.NotificationClientException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

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
    
    @Autowired
    AuditLogsRepository auditLogsRepository;

    static final String BEARER = "Bearer ";

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Autowired
    Environment environment;

    @PostMapping(
            value = "/adduser",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> addUser(@RequestHeader("UserPrinciple") HttpHeaders userPrinciple,
                                          @RequestBody UserRequest request) {

        log.info("{}::Before calling addUser", loggingComponentName);
        SearchUtils.adminRoleValidFromUserPrincipleObject(objectMapper,userPrinciple);
        String access_token = getBearerToken();
        AddUserRequest reqObj = new AddUserRequest(request.isAccountEnabled(),request.getSurName(),
                request.getDisplayName(),request.getMailNickname(),request.getUserPrincipalName(),
                request.getMobilePhone(),request.getPasswordProfile());
        UserResponse response =  userManagementService.addUser(access_token,reqObj);
        request.getGrpRoleIds().forEach(roleId -> {
             userManagementService.createGroupForUser(access_token, roleId, response.getId());
        });
        
      
     return ResponseEntity.status(201).body(response);
    }

    @PostMapping(
            value = "/invitation",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> sendUserInvitation(@RequestHeader("UserPrinciple") HttpHeaders userPrinciple,
                                                     @RequestBody UserInvitationRequest request) {

        log.info("{}::Before calling sendUserInvitation", loggingComponentName);
        SearchUtils.adminRoleValidFromUserPrincipleObject(objectMapper,userPrinciple);
        String access_token = getBearerToken();
        InvitationRequest invitationRequest = new InvitationRequest(request.getInvitedUserEmailAddress(), request.getInviteRedirectUrl(),request.isSendInvitationMessage());
        //get the user id in the response once user created successfully in the Azure Active directory
        UserResponse response =  userManagementService.inviteUser(access_token,invitationRequest);
        if (Objects.isNull(response) || Objects.isNull( response.getInvitedUser())) {
            throw new AccessManagementException(HttpStatus.valueOf(500),"user not created");
        }

        //assigning the role and group to the user once it's created.
        request.getGrpRoleIds().forEach(roleId -> {
            userManagementService.createGroupForUser(access_token, roleId, response.getInvitedUser().getId());
        });
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping(
            value = "/updateUser/{userId}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> updateUser(@RequestHeader("UserPrinciple") HttpHeaders userPrinciple,
                                             @PathVariable("userId") String userId,@RequestBody UpdateUserRequest request) {

        log.info("{}::Before calling updateUser", loggingComponentName);
        SearchUtils.adminRoleValidFromUserPrincipleObject(objectMapper,userPrinciple);
        String access_token = getBearerToken();
        int response =  userManagementService.updateUser(access_token,userId,request);
       
        return ResponseEntity.status(response).build();
    }

    @DeleteMapping(
            value = "/deleteuser/{userId}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> deleteUser(@RequestHeader("UserPrinciple") HttpHeaders userPrinciple,
                                          @PathVariable("userId") String userId) {

        log.info("{}::Before calling deleteUser", loggingComponentName);
        UserPrinciple userPrincipleObj = SearchUtils.adminRoleValidFromUserPrincipleObject(objectMapper,userPrinciple);
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
        SearchUtils.validateAdminGAApproverRoleFromUpObj(objectMapper,userPrinciple);
        String access_token = getBearerToken();
        log.info("{}:: After access_token in retrieveUserDetailsByGroupId",loggingComponentName);
        UserDetailsResponse response =  userManagementService.getUserRolesByGrpId(access_token,groupId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(
            value = "/users/{userId}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> retrieveUserDetailsById(@RequestHeader("userPrinciple") HttpHeaders userPrinciple,
                                                               @PathVariable("userId") String userId) {

        log.info("{}:: Before calling retrieveUserDetailsId",loggingComponentName);
        SearchUtils.adminRoleValidFromUserPrincipleObject(objectMapper,userPrinciple);
        String access_token = getBearerToken();
        log.info("{}:: After access_token in retrieveUserDetailsId",loggingComponentName);
        UserResponse response = userManagementService.getUserDetails(access_token,userId);
        UserRolesResponse roleResponse =  userManagementService.getUserGroup(access_token,userId);
        if (Objects.isNull(roleResponse) || CollectionUtils.isEmpty(roleResponse.getUserRoles())) {
            log.error("{}:: After roleResponse in retrieveUserDetailsId is null",loggingComponentName);
            throw new SearchResultNotFoundException("User group not found");
        }
        response.setRoleName(roleResponse.getUserRoles().stream().filter(
                userRole -> userRole.getPrincipalType().equalsIgnoreCase("GROUP"))
                .map(UserRoleResponse::getPrincipalDisplayName).findFirst().get());
        log.info("{}:: After setRoleName in retrieveUserDetailsId",loggingComponentName);
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

    @GetMapping(
            value = "/users",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> retrieveAllUserDetails(@RequestHeader("userPrinciple") HttpHeaders userPrinciple) {

        log.info("{} ::Before calling retrieveAllUserDetails",loggingComponentName);
        SearchUtils.adminRoleValidFromUserPrincipleObject(objectMapper,userPrinciple);
        String access_token = getBearerToken();
        UserDetailsResponse response =  userManagementService.getAllUsers(access_token);
        return ResponseEntity.status(200).body(response);
    }
    
    @PostMapping(
            value = "/feedback"
            
    )
    public ResponseEntity<Object> sendFeedBack(@RequestBody FeedbackRequest request) {

        log.info("{}::Before calling sendFeedBack",loggingComponentName);
        
        try {
      			EmailUtils.sendFeedBack(request.getFeedBack(),request.getComments(),environment.getProperty("apiKey"),environment.getProperty("feedback_template_id"));
		 } catch (NotificationClientException e) {
			
			log.error("error in sending feedback mail", e);
		}
	    
        return ResponseEntity.status(201).body("Success");
    }
}