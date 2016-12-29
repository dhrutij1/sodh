package com.sodh.com.sodh.service;

import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

/**
 * Created by dhruti on 29/12/16.
 */
public interface NotificationService {
	@Async
	void sendMail(List<String> to, List<String> ccList, Map<String, String> model, EmailTemplate template);

	void sendMail(String to, Map<String, String> model, EmailTemplate template);
}
