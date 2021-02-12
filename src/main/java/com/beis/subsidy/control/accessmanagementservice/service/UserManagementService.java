package com.beis.subsidy.control.accessmanagementservice.service;

import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;

public interface UserManagementService {

    UserDetailsResponse getAllUsers(String token);

    UserDetailsResponse getUserRolesByGrpId(String token, String userId);

    UserResponse addUser(String token, AddUserRequest request);

    int deleteUser(String token, String userId);
}
