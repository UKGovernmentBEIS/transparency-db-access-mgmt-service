package com.beis.subsidy.control.accessmanagementservice.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

@Slf4j
public class EmailUtils {

	public static void sendAwardNotificationEmail(String emailId, String status,Long awardNumber,
						 String approverName,Environment environment) throws NotificationClientException {

		String templateId = "";
		NotificationClient client = new NotificationClient(environment.getProperty("apiKey"));
		Map<String, Object> personalisation = new HashMap<>();
		personalisation.put("award_number", awardNumber);
		personalisation.put("approver_name", approverName);
		if (status.equals("Published")) {
			templateId = environment.getProperty("award_approved_template");
		} else if(status.equals("Rejected")) {
			templateId = environment.getProperty("award_reject_template");
		}
		SendEmailResponse response = client.sendEmail(templateId, emailId, personalisation, null);
		log.info("Email notification sent :: ");
	}


	
	public static void sendFeedBack(String feedBack,String comments,String apiKey,String template, Environment environment) throws NotificationClientException {
	
		log.info("inside  sendFeedBack ***** email * :: ");
		NotificationClient client = new NotificationClient(apiKey);
		String feedBackEmail=environment.getProperty("feedBackEmail");
		log.info("before sending ***** email **8 :: {}", feedBackEmail);
		if(StringUtils.isEmpty(comments)) {
			comments="N/A";
		}
		Map<String, Object> personalisation = new HashMap<>();
		personalisation.put("feedback_comment", feedBack);
		personalisation.put("comments", comments);
		log.info("before sending ***** email ** :: ");
		SendEmailResponse response = client.sendEmail(template, feedBackEmail, personalisation, null);

		log.info("email sent ::{}");
	}
	
}
