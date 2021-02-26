package com.beis.subsidy.control.accessmanagementservice.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FeedbackRequest {

	private String feedBack;
	private String comments;

	@JsonCreator
	public FeedbackRequest(@JsonProperty("feedBack") String feedBack, @JsonProperty("comments") String comments) {

		this.feedBack = feedBack;
		this.comments = comments;

	}
}
