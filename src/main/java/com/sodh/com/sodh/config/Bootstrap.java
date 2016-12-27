package com.sodh.com.sodh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by dhruti on 27/12/16.
 */
@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${bootstrap.database}")
	private Boolean bootstrap;

	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (bootstrap) {

		}
	}
}
