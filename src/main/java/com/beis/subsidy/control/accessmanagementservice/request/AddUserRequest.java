package com.beis.subsidy.control.accessmanagementservice.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddUserRequest {

    private boolean accountEnabled;

    private String surname;

    private String displayName;

    private String mailNickname;

    private String userPrincipalName;

    private String mobilePhone;

    @NotNull
    private PasswordProfile passwordProfile;

    @JsonCreator
    public AddUserRequest(
            @JsonProperty("accountEnabled") boolean accountEnabled,
            @JsonProperty("surname") String surname,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("mailNickname") String mailNickname,
            @JsonProperty("userPrincipalName") String userPrincipalName,
            @JsonProperty("mobilePhone") String mobilePhone,
            @JsonProperty("passwordProfile") PasswordProfile passwordProfile) {

        this.accountEnabled = accountEnabled;
        this.displayName = displayName;
        this.mailNickname = mailNickname;
        this.userPrincipalName = userPrincipalName;
        this.mobilePhone = mobilePhone;
        this.passwordProfile = passwordProfile;
        this.surname = surname;
    }
}
