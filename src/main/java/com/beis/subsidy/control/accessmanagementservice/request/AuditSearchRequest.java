package com.beis.subsidy.control.accessmanagementservice.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuditSearchRequest {

    
    private String searchName;

    private String searchStartDate;

    private String searchEndDate;

    private int pageNumber;

	private int totalRecordsPerPage;

	private String[] sortBy;

   
   
    }

