package com.beis.subsidy.control.accessmanagementservice.controller.feign;

import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.request.CreateUserInGroupRequest;
import com.beis.subsidy.control.accessmanagementservice.request.InvitationRequest;
import com.beis.subsidy.control.accessmanagementservice.request.UpdateUserRequest;
import feign.Headers;
import feign.RequestLine;
import feign.Response;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "GraphAPIFeignClient", url = "${graphApiUrl}")
public interface GraphAPIFeignClient {

    @GetMapping(value = "/v1.0/users?$top=999")
    @RequestLine("GET v1.0/users?$top=999")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getAllUserProfiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation);

    @PostMapping(value = "/v1.0/users")
    @RequestLine("POST v1.0/users")
    Response addUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                         @RequestBody AddUserRequest request);

    @DeleteMapping(value = "v1.0/users/{userId}")
    @RequestLine("DELETE v1.0/users")
    Response deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                        @PathVariable("userId")  String userId);

    @GetMapping(value = "/v1.0/groups/{groupId}/members")
    @RequestLine("GET /v1.0/groups/{groupId}/members")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getUsersByGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                               @PathVariable("groupId")  String groupId);

    @GetMapping(value = "/v1.0/users/{userId}/appRoleAssignments")
    @RequestLine("GET /v1.0/users/{userId}/appRoleAssignments")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getUserGroupName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                               @PathVariable("userId")  String userId);

    @GetMapping(value = "/v1.0/users/{id}")
    @RequestLine("GET /v1.0/users/{id}")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                              @PathVariable("id")  String id);

    @PostMapping(value = "/v1.0/groups/{groupId}/members/$ref")
    @RequestLine("POST /v1.0/groups/{groupId}/members/$ref")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response createGroupForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                               @PathVariable("groupId")  String groupId,
                                @RequestBody CreateUserInGroupRequest request);

    @PostMapping(value = "/v1.0/invitations")
    @RequestLine("POST v1.0/invitations")
    Response inviteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                        @RequestBody InvitationRequest request);

    @PatchMapping(value = "/v1.0/users/{id}")
    @RequestLine("PATCH v1.0/users/{id}")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response updateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                        @PathVariable("id")  String id,@RequestBody UpdateUserRequest request);

    @GetMapping(value = "/v1.0/users/{id}/memberOf")
    @RequestLine("GET v1.0/users/{id}/memberOf")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getUserGroups(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                           @PathVariable("id")  String id);

    @GetMapping(value = "/v1.0/groups/{groupId}/members/$count")
    @RequestLine("GET /v1.0/groups/{groupId}/members/$count")
    @Headers({"Authorization: {authorization}","Content-Type: text/plain"})
    Response countUsersByGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION)String authorisation,
                               @PathVariable("groupId")  String groupId,
                                 @RequestHeader("consistencyLevel") String consistencyLevel
    );

    @GetMapping(value = "/v1.0/groups?$filter=displayName eq '{displayName}'")
    @RequestLine("GET /v1.0/groups?$filter=displayName eq '{displayName}'")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getGroupByDisplayName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                               @PathVariable("displayName")  String displayName);

}
