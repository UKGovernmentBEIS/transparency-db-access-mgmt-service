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

    private String surname;

    private String mobilePhone;

    private String firstName;

    @JsonCreator
    public UpdateUserRequest(
            @JsonProperty("surname") String surname,
            @JsonProperty("mobilePhone") String mobilePhone,
            @JsonProperty("givenName") String firstName) {

        this.surname = surname;
        this.mobilePhone= mobilePhone;
        this.firstName = firstName;
    }
}
