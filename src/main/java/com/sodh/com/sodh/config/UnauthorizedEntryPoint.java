package com.sodh.com.sodh.config;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dhruti on 27/12/16.
 */
@Component("UnauthorizedEntryPoint")
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {
	private static Logger logger = LoggerFactory.getLogger(UnauthorizedEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		logger.debug("inside UnauthorizedEntryPoint...");
		logger.debug("exception: {}", authException);

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"Unauthorized: Authentication info was either missing or invalid.");
	}

}
