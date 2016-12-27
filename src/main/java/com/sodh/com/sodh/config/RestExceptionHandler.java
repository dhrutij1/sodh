package com.sodh.com.sodh.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dhruti on 27/12/16.
 */
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<BusinessError> handleException(Exception ex) {
		logger.debug("in the ExceptionHandlerController..{}", ex);
		return PfmException.handleException(ex);
	}
}
