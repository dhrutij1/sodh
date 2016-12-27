package com.sodh.com.sodh.config;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Created by dhruti on 27/12/16.
 */
import javax.servlet.Filter;

public class WebSecurityCorsFilter implements Filter {
	private static final String[] ALLOW_DOMAINS;
	private static Logger logger = LoggerFactory.getLogger(WebSecurityCorsFilter.class);

	static {
		InputStream inputStream = null;
		try {
			inputStream = new ClassPathResource("application.properties")
					.getInputStream();
			Properties props = new Properties();
			props.load(inputStream);
			String field = "allow.origins";
			if (!props.containsKey(field))
				throw new PfmException("Property allow.origins not found in property file.",
						PfmExceptionCode.PROPERTY_MISSING);
			String allowOrigins = props.getProperty(field);
			ALLOW_DOMAINS = allowOrigins.split("\\s*,\\s*");
		} catch (IOException e) {
			throw new PfmException("Cannot read from property file.", PfmExceptionCode.READ_PROPERTY_ERROR);
		} finally {
			// apache commons / IO
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		// can be moved to properties
		//String[] allowDomain = {"http://localhost:8080", "http://127.0.0.1:8080"};

		String originHeader = request.getHeader("origin");
		logger.debug("header is {}", originHeader);

		if (originHeader != null) {
			for (String domain : ALLOW_DOMAINS) {
				if (originHeader.endsWith(domain)) {
					logger.debug("end with domain...");
					res.setHeader("Access-Control-Allow-Origin", originHeader);
					break;
				}
			}

			//res.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:8080");
			res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
			res.setHeader("Access-Control-Max-Age", "3600");
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Headers", "Authorization, X-XSRF-TOKEN, Content-Type, " +
					"Accept, x-requested-with, Cache-Control");
		}

		chain.doFilter(request, res);
	}

	@Override
	public void destroy() {
	}
}
