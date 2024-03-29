package com.beis.subsidy.control.accessmanagementservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupResponse {

    @JsonProperty
    private String id;

    @JsonProperty
    private String displayName;
}
