package com.beis.subsidy.control.accessmanagementservice.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    private String givenName;

    private String surname;

    private String mobilePhone;

    private String displayName;

    //private String userPrincipalName;

    @JsonCreator
    public UpdateUserRequest(
            @JsonProperty("surname") String surname,
            @JsonProperty("mobilePhone") String mobilePhone,
            @JsonProperty("displayName") String displayName) {

        this.surname = surname;
        this.mobilePhone= mobilePhone;
        this.displayName = displayName;
    }
}
