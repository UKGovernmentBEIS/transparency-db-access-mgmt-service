package com.beis.subsidy.control.accessmanagementservice.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserInvitationRequest {


    private String invitedUserDisplayName;

    private String surname;

    private String invitedUserEmailAddress;

    private String inviteRedirectUrl;

    private boolean  sendInvitationMessage;
    private String mobilePhone;

    private Set<String> grpRoleIds;

    @JsonCreator
    public UserInvitationRequest(
            @JsonProperty("invitedUserDisplayName") String invitedUserDisplayName,
            @JsonProperty("invitedUserEmailAddress") String invitedUserEmailAddress,
            @JsonProperty("inviteRedirectUrl") String inviteRedirectUrl,
            @JsonProperty("sendInvitationMessage") boolean sendInvitationMessage,
            @JsonProperty("mobilePhone") String mobilePhone,
            @JsonProperty("grpRoleIds") Set<String> grpRoleIds) {

        this.inviteRedirectUrl = inviteRedirectUrl;
        this.invitedUserDisplayName = invitedUserDisplayName;
        this.invitedUserEmailAddress = invitedUserEmailAddress;
        this.sendInvitationMessage =  sendInvitationMessage;
        this.mobilePhone = mobilePhone;
        this.grpRoleIds = grpRoleIds;
    }
}
