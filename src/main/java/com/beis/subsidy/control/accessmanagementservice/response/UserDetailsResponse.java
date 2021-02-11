package com.beis.subsidy.control.accessmanagementservice.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UserDetailsResponse {

    @JsonProperty
    private List<UserResponse> users = new ArrayList<>();

    @JsonGetter("value")
    public List<UserResponse> getUserProfiles() {
        return users;
    }

    @JsonSetter("users")
    public void setUserProfiles(List<UserResponse> value) {
        this.users = users;
    }

  }
