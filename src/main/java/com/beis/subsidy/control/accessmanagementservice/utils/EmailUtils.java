package com.beis.subsidy.control.accessmanagementservice.utils;

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
        SendEmailResponse response = client.sendEmail(
        		environment.getProperty("templateId"),
                emailId,
                null,
                null
        );
    	
    	
        log.info("response :: "+response.getBody());
    }
}
