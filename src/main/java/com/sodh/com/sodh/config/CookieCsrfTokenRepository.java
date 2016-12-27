package com.sodh.com.sodh.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Created by dhruti on 12/27/16.
 */
public final class CookieCsrfTokenRepository implements CsrfTokenRepository {
	static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";
	static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";
	static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";
	private static final String COOKIE_DOMAIN;
	private static Logger logger = LoggerFactory.getLogger(CookieCsrfTokenRepository.class);

	static {
		InputStream inputStream = null;
		try {
			inputStream = new ClassPathResource("application.properties")
					.getInputStream();
			Properties props = new Properties();
			props.load(inputStream);
			String field = "cookie.domain";
			if (!props.containsKey(field))
				throw new PfmException("Property cookie.domain not found in property file.",
						PfmExceptionCode.PROPERTY_MISSING);
			COOKIE_DOMAIN = props.getProperty(field);
		} catch (IOException e) {
			throw new PfmException("Cannot read from property file.", PfmExceptionCode.READ_PROPERTY_ERROR);
		} finally {
			// apache commons / IO
			IOUtils.closeQuietly(inputStream);
		}
	}

	private final Method setHttpOnlyMethod;
	private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;
	private String headerName = DEFAULT_CSRF_HEADER_NAME;
	private String cookieName = DEFAULT_CSRF_COOKIE_NAME;
	private boolean cookieHttpOnly;

	public CookieCsrfTokenRepository() {
		this.setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly", boolean.class);
		if (this.setHttpOnlyMethod != null) {
			this.cookieHttpOnly = true;
		}
	}

	/**
	 * Factory method to conveniently create an instance that has
	 * {@link #setCookieHttpOnly(boolean)} set to false.
	 *
	 * @return and instance of CookieCsrfTokenRepository with
	 * {@link #setCookieHttpOnly(boolean)} set to false
	 */
	public static CookieCsrfTokenRepository withHttpOnlyFalse() {
		CookieCsrfTokenRepository result = new CookieCsrfTokenRepository();
		result.setCookieHttpOnly(false);
		return result;
	}

	@Override
	public CsrfToken generateToken(HttpServletRequest request) {
		return new DefaultCsrfToken(this.headerName, this.parameterName,
				createNewToken());
	}

	@Override
	public void saveToken(CsrfToken token, HttpServletRequest request,
						  HttpServletResponse response) {
		logger.debug("cookie domain is {}", COOKIE_DOMAIN);
		String tokenValue = token == null ? "" : token.getToken();
		Cookie cookie = new Cookie(this.cookieName, tokenValue);
		cookie.setSecure(request.isSecure());
		cookie.setPath(getCookiePath(request));
		cookie.setDomain(COOKIE_DOMAIN);
		if (token == null) {
			cookie.setMaxAge(0);
		} else {
			cookie.setMaxAge(-1);
		}
		if (cookieHttpOnly && setHttpOnlyMethod != null) {
			ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
		}

		response.addCookie(cookie);
	}

	@Override
	public CsrfToken loadToken(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, this.cookieName);
		if (cookie == null) {
			return null;
		}
		String token = cookie.getValue();
		if (!StringUtils.hasLength(token)) {
			return null;
		}
		return new DefaultCsrfToken(this.headerName, this.parameterName, token);
	}

	/**
	 * Sets the name of the HTTP request parameter that should be used to provide a token.
	 *
	 * @param parameterName the name of the HTTP request parameter that should be used to
	 *                      provide a token
	 */
	public void setParameterName(String parameterName) {
		Assert.notNull(parameterName, "parameterName is not null");
		this.parameterName = parameterName;
	}

	/**
	 * Sets the name of the HTTP header that should be used to provide the token.
	 *
	 * @param headerName the name of the HTTP header that should be used to provide the
	 *                   token
	 */
	public void setHeaderName(String headerName) {
		Assert.notNull(headerName, "headerName is not null");
		this.headerName = headerName;
	}

	/**
	 * Sets the name of the cookie that the expected CSRF token is saved to and read from.
	 *
	 * @param cookieName the name of the cookie that the expected CSRF token is saved to
	 *                   and read from
	 */
	public void setCookieName(String cookieName) {
		Assert.notNull(cookieName, "cookieName is not null");
		this.cookieName = cookieName;
	}

	/**
	 * Sets the HttpOnly attribute on the cookie containing the CSRF token.
	 * The cookie will only be marked as HttpOnly if both <code>cookieHttpOnly</code>
	 * is <code>true</code> and the underlying version of Servlet is 3.0 or greater.
	 * Defaults to <code>true</code> if the underlying version of Servlet is 3.0 or greater.
	 * NOTE: The {@link Cookie#setHttpOnly(boolean)} was introduced in Servlet 3.0.
	 *
	 * @param cookieHttpOnly <code>true</code> sets the HttpOnly attribute,
	 *                       <code>false</code> does not set it (depending on Servlet version)
	 * @throws IllegalArgumentException if <code>cookieHttpOnly</code> is <code>true</code>
	 *                                  and the underlying version of Servlet is less than 3.0
	 */
	public void setCookieHttpOnly(boolean cookieHttpOnly) {
		if (cookieHttpOnly && setHttpOnlyMethod == null) {
			throw new IllegalArgumentException("Cookie will not be marked as HttpOnly " +
					"because you are using a version of Servlet less than 3.0. " +
					"NOTE: The Cookie#setHttpOnly(boolean) was introduced in Servlet 3.0.");
		}
		this.cookieHttpOnly = cookieHttpOnly;
	}

	private String getCookiePath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}

	private String createNewToken() {
		return UUID.randomUUID().toString();
	}
}