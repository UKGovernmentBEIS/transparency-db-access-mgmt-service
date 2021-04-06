package com.beis.subsidy.control.accessmanagementservice.service.impl;

import static com.beis.subsidy.control.accessmanagementservice.utils.JsonFeignResponseUtil.toResponseEntity;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPIFeignClient;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessManagementException;
import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.request.CreateUserInGroupRequest;
import com.beis.subsidy.control.accessmanagementservice.request.InvitationRequest;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRolesResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRoleResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;
import com.beis.subsidy.control.accessmanagementservice.service.UserManagementService;
import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    GraphAPIFeignClient graphAPIFeignClient;

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    @Value("${graphApiUrl}")
    private String graphApiUrl;

    /**
     * Get the group info
     * @param token
     * @param groupId
     * @return
     */
    @Override
    public UserDetailsResponse getUserRolesByGrpId(String token, String groupId) {
        // Graph API call.
        UserDetailsResponse userDetailsResponse = null;
        Response response = null;
        Object clazz;
        try {
            long time1 = System.currentTimeMillis();
            log.info("{}::before calling to getUsersByGroupId  Graph Api",loggingComponentName);
            response = graphAPIFeignClient.getUsersByGroupId("Bearer " + token,groupId);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 200) {
                clazz = UserDetailsResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userDetailsResponse
                        = (UserDetailsResponse) responseResponseEntity.getBody();
                if (Objects.nonNull(userDetailsResponse)) {
                    mapGroupInfoToUser(token,userDetailsResponse.getUserProfiles());
                }

            } else if (response.status() == 404) {
                throw new SearchResultNotFoundException("Group Id not found");
            } else {
                log.error("{} ::get user details by groupId Graph Api is failed ::{}",loggingComponentName,response.status());
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

    public  void mapGroupInfoToUser(String token, List<UserResponse> userProfiles) {

       // final UserRolesResponse userResponse;
        log.info("{}::before calling toGraph Api in the mapGroupInfoToUser",loggingComponentName);
        userProfiles.forEach(userProfile -> {
            UserRolesResponse userRolesResponse = getUserGroup(token,userProfile.getId());
            String roleName = userRolesResponse.getUserRoles().stream().filter(
                    userRole -> userRole.getPrincipalType().equalsIgnoreCase("GROUP"))
                    .map(UserRoleResponse::getPrincipalDisplayName).findFirst().get();
           if(!StringUtils.isEmpty(roleName)) {

               userProfile.setRoleName(roleName);
           }

        });
    }

   /**
     * Delete the user based on the user id
     * @param token
     * @param userId
     * @return
     */
    @Override
    public int deleteUser(String token, String userId) {
        Response response = null;
        int status = 0;
        try {
            response = graphAPIFeignClient.deleteUser("Bearer " + token, userId);
            log.info("{}:: After deleteUser Graph Api call {}", loggingComponentName);

            if (response.status() == 204 || response.status() == 404) {
                status = response.status();
            } else {
                log.error("{}:: Graph Api failed:: status code {} & unable to delete the user {}",
                        loggingComponentName,  response.status());
                throw new AccessManagementException(HttpStatus.valueOf(response.status()),"unable to delete the user profile");
            }

        } catch (FeignException ex) {
            log.error("{}:: Graph Api failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Delete User Graph Api failed");
        }
        return status;
    }

    /**
     * This method is used to get the user group based on the userId
     * @param token
     * @param userId
     * @return
     */
    public UserResponse getUserDetails(String token, String userId) {

        UserResponse userResponse = null;
        Response response = null;
        Object clazz;
        String groupName = null;
        try {
            long time1 = System.currentTimeMillis();
            log.info("Before calling to Graph Api getUserDetails");
            response = graphAPIFeignClient.getUserDetails("Bearer " + token,userId);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 200) {
                clazz = UserResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userResponse
                        = (UserResponse) responseResponseEntity.getBody();
            }

        } catch (FeignException ex) {
            log.error("{}:: get  userdetail Graph Api is failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userResponse;
    }

    @Override
    public int createGroupForUser(String token, String groupId, String id) {
        Response response = null;
        int status = 0;
        Object clazz;
        String graphReq= "https://graph.microsoft.com/v1.0/directoryObjects/{id}";
        try {
            String req =  graphReq.replace("id", id);
            CreateUserInGroupRequest request = new  CreateUserInGroupRequest(req);
            response = graphAPIFeignClient.createGroupForUser("Bearer " + token,groupId,request);
            if (response.status() == 204) {
                 status = response.status();
            } else if (response.status() == 400 || response.status() == 417) {
                log.error("{}:: Graph Api  createGroupForUser:: status code {}",
                        loggingComponentName, response.status());
                throw new InvalidRequestException("create createGroupForUser request is invalid");
            } else {
                log.error("{}:: Graph Api  createGroupForUser:: status code {}",
                        loggingComponentName, response.status());
                throw new AccessManagementException(HttpStatus.valueOf(response.status()), "Create createGroupForUser Graph Api Failed");
            }

        } catch (FeignException ex) {
            log.error("{}:: Graph Api failed createGroupForUser:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }

        return status;
    }

    /**
     * This method is used to get the user group based on the userId
     * @param token
     * @param userId
     * @return
     */
    public UserRolesResponse getUserGroup(String token, String userId) {
        // Graph API call.
        UserRolesResponse userRolesResponse = null;
        Response response = null;
        Object clazz;
        String groupName = null;
        try {
            log.info("{}::Before calling to Graph Api getUserGroupName and user id is {}",loggingComponentName, userId);
            response = graphAPIFeignClient.getUserGroupName("Bearer " + token,userId);
            log.info("{}:: After the call Graph Api getUserGroupName and  status is {}", loggingComponentName,response.status());

            if (response.status() == 200) {
                clazz = UserRolesResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userRolesResponse
                        = (UserRolesResponse) responseResponseEntity.getBody();
            }

        } catch (FeignException ex) {
            log.error("{}:: get  groupId Graph Api is failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userRolesResponse;
    }

    @Override
    public UserDetailsResponse getAllUsers(String token) {

        // Graph API call.
        UserDetailsResponse userDetailsResponse = null;
        Response response = null;
        Object clazz;
        try {
            long time1 = System.currentTimeMillis();
            response = graphAPIFeignClient.getAllUserProfiles("Bearer " + token);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 200) {
                clazz = UserDetailsResponse.class;
                ResponseEntity<Object> responseResponseEntity = toResponseEntity(response, clazz);
                userDetailsResponse
                        = (UserDetailsResponse) responseResponseEntity.getBody();

            } else if (response.status() == 404) {
                throw new SearchResultNotFoundException("get users not found");
            }

        } catch (FeignException ex) {
            log.error("{}:: UserProfile api failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userDetailsResponse;
    }

    @Override
    public UserResponse inviteUser(String token, InvitationRequest invitationRequest) {
        Response response = null;
        int status = 0;
        UserResponse userResponse;
        Object clazz;
        try {
            response = graphAPIFeignClient.inviteUser("Bearer " + token, invitationRequest);
            log.info("{}:: after invite User Graph Api call {}", loggingComponentName,response);

            if (response != null && response.status() == 201) {
                log.info("{}:: after response  and inside if {}", loggingComponentName,response.status());
                clazz = UserResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userResponse
                        = (UserResponse) responseResponseEntity.getBody();
                status = response.status();
            } else if (response != null && response.status() == 400) {

                throw new InvalidRequestException("invite user request is invalid");
            } else {
                log.error("{}:: Graph Api  inviteUser:: status code {}",
                        loggingComponentName, response.status());
                throw new AccessManagementException(HttpStatus.valueOf(response.status()), "Create User Graph Api Failed");
            }

        } catch (FeignException ex) {
            log.error("{}:: Graph Api failed inviteUser:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userResponse;
    }

    @Override
    public int updateUser(String token, String userId, UpdateUserRequest request) {

        Response response = null;
        try {
            log.info("{}:: before updateUser User Graph Api call", loggingComponentName);
            response = graphAPIFeignClient.updateUser("Bearer " + token, userId,request);
            log.info("{}:: after updateUser User Graph Api call & status is {}", loggingComponentName,response.status());
            if (response.status() == 400) {

                    throw new InvalidRequestException("updateUser request is invalid");

            } else if (response.status() > 400) {
                log.error("{}:: Graph Api  updateUser:: status code {}",
                        loggingComponentName, response.status());
                throw new AccessManagementException(HttpStatus.valueOf(response.status()), "updateUser User Graph Api Failed");
            }

        } catch (FeignException ex) {
            log.error("{}:: Graph Api failed updateUser:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return response.status();
    }
}
