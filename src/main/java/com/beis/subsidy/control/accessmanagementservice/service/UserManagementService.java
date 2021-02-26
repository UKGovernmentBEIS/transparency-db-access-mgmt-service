package com.beis.subsidy.control.accessmanagementservice.service;

import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.request.InvitationRequest;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.UserDetailsResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserResponse;
import com.beis.subsidy.control.accessmanagementservice.response.UserRolesResponse;

public interface UserManagementService {

    UserDetailsResponse getUserRolesByGrpId(String token, String userId);

    UserResponse addUser(String token, AddUserRequest request);

    UserRolesResponse getUserGroup(String token, String userId);

    int deleteUser(String token, String userId);

    UserResponse getUserDetails(String token, String userId);

    int createGroupForUser(String token, String gaId, String id);

    UserDetailsResponse getAllUsers(String token);

    UserResponse inviteUser(String access_token, InvitationRequest invitationRequest);

    int  updateUser(String access_token, String userId, UpdateUserRequest request);
}
