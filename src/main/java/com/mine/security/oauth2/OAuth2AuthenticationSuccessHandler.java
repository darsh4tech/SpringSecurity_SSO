package com.mine.security.oauth2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.UriComponentsBuilder;

import com.mine.config.AppProperties;
import com.mine.security.jwt.TokenProvider;
import com.mine.utils.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

	private static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

	private TokenProvider tokenProvider;

	private AppProperties appProperties;

	private CookieUtils cookieUtils;

	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	private HandlerExceptionResolver handlerExceptionResolver;
	
	private SecurityContextRepository securityContextRepository;
	
	OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties,
			HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
			CookieUtils cookieUtils, HandlerExceptionResolver handlerExceptionResolver, SecurityContextRepository securityContextRepository) {
		this.tokenProvider = tokenProvider;
		this.appProperties = appProperties;
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
		this.cookieUtils = cookieUtils;
		this.handlerExceptionResolver = handlerExceptionResolver;
		this.securityContextRepository = securityContextRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		logger.info("onAuthenticationSuccess - authentication : {}",authentication);
		SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
	    SecurityContext context = securityContextHolderStrategy.createEmptyContext();
	    context.setAuthentication(authentication);
	    securityContextHolderStrategy.setContext(context);
	    securityContextRepository.saveContext(context, request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		Optional<String> redirectUri = cookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			handlerExceptionResolver.resolveException(request, response, null, new BadRequestException(
					"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication"));
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		String accessToken = tokenProvider.createToken(authentication);
		String refreshToken = tokenProvider.generateRefreshToken(authentication);

		return UriComponentsBuilder.fromUriString(targetUrl).queryParam("accessToken", accessToken).queryParam("refreshToken", refreshToken).build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	@SneakyThrows(MalformedURLException.class)
	private boolean isAuthorizedRedirectUri(String uri) {

		URL clientRedirectUri = new URL(uri);
		logger.info("clientRedirectUri : {} - Port : {} - protocol : {}", clientRedirectUri,
				clientRedirectUri.getPort(), clientRedirectUri.getProtocol());

		return appProperties.getOauth2().getAuthorizedRedirectUris().stream().map(authRedirectUri -> {
			try {
				return new URL(authRedirectUri);
			} catch (MalformedURLException e) {
				logger.error("MalformedURLException : {}", e);
			}
			return null;
		}).anyMatch(authorizedURI -> authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
				&& authorizedURI.getProtocol().equalsIgnoreCase(clientRedirectUri.getProtocol())
				&& authorizedURI.getPort() == clientRedirectUri.getPort());
	}
		
}