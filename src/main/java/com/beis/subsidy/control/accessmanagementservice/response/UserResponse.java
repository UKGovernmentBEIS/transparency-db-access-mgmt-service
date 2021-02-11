package com.beis.subsidy.control.accessmanagementservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    @JsonProperty
    private String id;

    @JsonProperty
    private String displayName;

    @JsonProperty
    private String givenName;

    @JsonProperty
    private String jobTitle;

    @JsonProperty
    private String mail;

    @JsonProperty
    private String mobilePhone;

    @JsonProperty
    private String surname;

    @JsonProperty
    private String userPrincipalName;


  /*  public UserResponse(String id, String displayName, String givenName, String jobTitle,
    String mobilePhone, String mail, String userPrincipalName) {

        this.id = id;
        this.displayName = displayName;
        this.givenName = givenName;
        this.jobTitle = jobTitle;
        this.mobilePhone = mobilePhone;
        this.mail = mail;
        this.userPrincipalName = userPrincipalName;

    }*/

  }
