package com.beis.subsidy.control.accessmanagementservice.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class InvitationRequest {


    private String invitedUserEmailAddress;

    private String inviteRedirectUrl;

    private boolean  sendInvitationMessage;

    @JsonCreator
    public InvitationRequest(
            @JsonProperty("invitedUserEmailAddress") String invitedUserEmailAddress,
            @JsonProperty("inviteRedirectUrl") String inviteRedirectUrl,
            @JsonProperty("sendInvitationMessage") boolean sendInvitationMessage) {

        this.inviteRedirectUrl = inviteRedirectUrl;
        this.invitedUserEmailAddress = invitedUserEmailAddress;
        this.sendInvitationMessage =  sendInvitationMessage;
    }
}
