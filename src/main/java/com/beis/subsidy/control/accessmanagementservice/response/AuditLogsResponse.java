package com.beis.subsidy.control.accessmanagementservice.response;

import com.beis.subsidy.control.accessmanagementservice.model.AuditLogs;
import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.utils.SearchUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogsResponse {
    @JsonProperty
    private String userName;

    @JsonProperty
    private String grantingAuthority;

    @JsonProperty
    private String eventType;
    
    @JsonProperty
    private String eventId;

    @JsonProperty
    private LocalDateTime actionDateTime;

    
    @JsonProperty
    private String gaName;

    @JsonProperty
    private String eventMessage;
   

    

    public AuditLogsResponse(AuditLogs auditLogs) {
        this.userName = auditLogs.getUserName();
        this.gaName = auditLogs.getGaName();
        this.eventType = auditLogs.getEventType();
        this.eventId = auditLogs.getEventId();
        this.eventMessage = auditLogs.getEventMessage();
        this.actionDateTime = auditLogs.getCreatedTimestamp();
      
       
    }
}
