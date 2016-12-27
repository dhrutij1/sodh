package com.sodh.com.sodh.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhruti on 27/12/16.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class)
				.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/api/v1/user/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/emailVerification/{.*}").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/user/securityQuestions").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/accountHelp/securityQuestions").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/accountHelp/securityAnswers").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/newPassword").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/checkExistingUserName").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/csrf").permitAll()
				.antMatchers(HttpMethod.DELETE, "/api/v1/user?username={.*}").permitAll()
				.antMatchers("/api/**").authenticated()
				.and()
				.httpBasic().disable();

		http.sessionManagement().maximumSessions(1);
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
	}

}
