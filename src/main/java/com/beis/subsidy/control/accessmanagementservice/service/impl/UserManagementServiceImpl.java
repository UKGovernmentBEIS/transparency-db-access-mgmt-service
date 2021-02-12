package com.beis.subsidy.control.accessmanagementservice.service.impl;

import com.beis.subsidy.control.accessmanagementservice.controller.feign.GraphAPIFeignClient;
import com.beis.subsidy.control.accessmanagementservice.exception.AccessManagementException;
import com.beis.subsidy.control.accessmanagementservice.exception.InvalidRequestException;
import com.beis.subsidy.control.accessmanagementservice.exception.SearchResultNotFoundException;
import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRoleResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRolesResponse;
import com.beis.subsidy.control.accessmanagementservice.service.UserManagementService;
import static com.beis.subsidy.control.accessmanagementservice.utils.JsonFeignResponseUtil.toResponseEntity;
import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    @Override
    public UserDetailsResponse getAllUsers(String token) {

        // Graph API call.
        UserDetailsResponse userDetailsResponse = null;
        Response response = null;
        Object clazz;
        try {
            long time1 = System.currentTimeMillis();
            log.info("{}:: before calling toGraph Api is", loggingComponentName);
            response = graphAPIFeignClient.getAllUserProfiles("Bearer " + token);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 200) {
                clazz = UserDetailsResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userDetailsResponse
                        = (UserDetailsResponse) responseResponseEntity.getBody();

            } else if (response.status() == 404) {
                throw new SearchResultNotFoundException("get users not found");
            } else {
                log.error("Graph Api is failed ::{}",response.status());
            }

        } catch (FeignException ex) {
            log.error("{}:: UserProfile api failed:: status code {} & message {}",
                        loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userDetailsResponse;
    }

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
            log.info("{}::before calling toGraph Api is",loggingComponentName);
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

    public  void mapGroupInfoToUser(String token, List<UserResponse> userProfiles) {
        log.info("{}::before calling toGraph Api in the mapGroupInfoToUser",loggingComponentName);
        userProfiles.forEach(userProfile -> {
           String groupName = getUserGroup(token,userProfile.getId());
           if(!StringUtils.isEmpty(groupName)) {
               userProfile.setUserPrincipalName(groupName);
           }

        });
    }

    /**
     * Add the user in the Azure group
     * @param token
     * @param request
     * @return
     */
    @Override
    public UserResponse addUser(String token, AddUserRequest request) {
        Response response = null;
        int status = 0;
        UserResponse userResponse;
        Object clazz;
        try {
            long time1 = System.currentTimeMillis();
            response = graphAPIFeignClient.addUser("Bearer " + token, request);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 201) {
                clazz = UserResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userResponse
                        = (UserResponse) responseResponseEntity.getBody();
                status = response.status();
            } else if (response.status() == 400) {
                throw new InvalidRequestException("create user request is invalid");
            } else {
                log.error("{}:: Graph Api failed:: status code {}",
                        loggingComponentName, 500);
                throw new AccessManagementException(HttpStatus.valueOf(500), "Create User Graph Api Failed");
            }

        } catch (FeignException ex) {
            log.error("{}:: Graph Api failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return userResponse;
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
            long time1 = System.currentTimeMillis();
            response = graphAPIFeignClient.deleteUser("Bearer " + token, userId);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 204) {
                status = response.status();
            } else{
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
    public String getUserGroup(String token, String userId) {
        // Graph API call.
        UserRolesResponse userRolesResponse = null;
        Response response = null;
        Object clazz;
        String groupName = null;
        try {
            long time1 = System.currentTimeMillis();
            log.info("Before calling to Graph Api getUserGroupName");
            response = graphAPIFeignClient.getUserGroupName("Bearer " + token,userId);
            log.info("{}:: Time taken to call Graph Api is {}", loggingComponentName, (System.currentTimeMillis() - time1));

            if (response.status() == 200) {
                clazz = UserRolesResponse.class;
                ResponseEntity<Object> responseResponseEntity =  toResponseEntity(response, clazz);
                userRolesResponse
                        = (UserRolesResponse) responseResponseEntity.getBody();
                groupName = userRolesResponse.getUserRoles().stream().filter(
                        userRole -> userRole.getPrincipalType().equalsIgnoreCase("GROUP"))
                        .map(UserRoleResponse::getPrincipalDisplayName).findFirst().get();
            }

        } catch (FeignException ex) {
            log.error("{}:: get  groupId Graph Api is failed:: status code {} & message {}",
                    loggingComponentName, ex.status(), ex.getMessage());
            throw new AccessManagementException(HttpStatus.valueOf(ex.status()), "Graph Api failed");
        }
        return groupName;
    }
}
