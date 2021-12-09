package com.beis.subsidy.control.accessmanagementservice.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class UserGroupsResponse {

    @JsonProperty
    private List<UserGroupResponse> value = new ArrayList<>();

    @JsonGetter("value")
    public List<UserGroupResponse> getUserGroups() {
        return value;
    }

    @JsonSetter("value")
    public void setUserProfiles(List<UserGroupResponse> value) {
        this.value = value;
    }

}
