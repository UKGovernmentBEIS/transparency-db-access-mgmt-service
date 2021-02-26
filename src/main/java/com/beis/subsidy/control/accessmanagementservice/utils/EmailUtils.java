package com.beis.subsidy.control.accessmanagementservice.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;



import lombok.extern.slf4j.Slf4j;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

@Slf4j
public class EmailUtils {

	@Autowired
	static Environment environment;

	public static void sendEmail(String emailId) throws NotificationClientException {
		NotificationClient client = new NotificationClient(environment.getProperty("apiKey"));
		SendEmailResponse response = client.sendEmail(environment.getProperty("templateId"), emailId, null, null);

		log.info("response :: " + response.getBody());
	}

	public static void sendEmail(String emailId, String passWord) throws NotificationClientException {

		Map<String, Object> personalisation = new HashMap<>();
		personalisation.put("default_pass", passWord);

		NotificationClient client = new NotificationClient(environment.getProperty("apiKey"));
		SendEmailResponse response = client.sendEmail(environment.getProperty("new-user-mail-template"), emailId,
				personalisation, null);

		log.info("response :: " + response.getBody());
	}
	
	
	public static void sendFeedBack(String feedBack,String comments) throws NotificationClientException {
		
		NotificationClient client = new NotificationClient(environment.getProperty("apiKey"));
		//NotificationClient client = new NotificationClient("beis_notification-acabb994-cf6a-4d65-8632-1cc3ece74aa5-ef624de5-91dd-4f1b-8279-d970ee3949d5");
		String feedBackEmail ="krishna.vamsiparankusam@cognizant.com";
		//String feedBackEmail="subsidycontrol@beis.gov.uk";
		//comments="Satisfied";
		if(StringUtils.isEmpty(comments)) {
			comments="N/A";
		}
		Map<String, Object> personalisation = new HashMap<>();
		personalisation.put("feedback_comment", feedBack);
		personalisation.put("comments", comments);
		//environment.getProperty("feed_back_template");
		SendEmailResponse response = client.sendEmail("a6e03ad7-6142-4a40-a04c-1f991079d266", feedBackEmail, personalisation, null);

		log.info("response :: " + response.getBody());
	}

public static void main(String a[]) throws NotificationClientException {
		
		EmailUtils.sendFeedBack("Satisfied", "");
	}
}
