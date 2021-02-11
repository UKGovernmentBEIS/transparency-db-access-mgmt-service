package com.beis.subsidy.control.accessmanagementservice.service;

import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;

import java.util.List;

public interface UserManagementService {

    UserDetailsResponse getAllUsers(String token);

    UserResponse addUser(String token, AddUserRequest request);

    int deleteUser(String token, String userId);
}
