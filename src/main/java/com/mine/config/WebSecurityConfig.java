package com.mine.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.client.RestTemplate;

import com.mine.dto.UserRoleEnum;
import com.mine.security.error.handler.CustomAccessDeinedHandler;
import com.mine.security.error.handler.RestAuthenticationEntryPoint;
import com.mine.security.jwt.TokenAuthenticationFilter;
import com.mine.security.oauth2.CustomOAuth2UserService;
import com.mine.security.oauth2.CustomOidcUserService;
import com.mine.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mine.security.oauth2.OAuth2AccessTokenResponseConverterWithDefaults;
import com.mine.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.mine.security.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	CustomOidcUserService customOidcUserService;

	@Autowired
	OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Autowired
	OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Autowired
	SecurityContextRepository securityContextRepository;
	
	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
//		.requiresChannel((requiresChannel) ->
//			requiresChannel
//				.anyRequest().requiresSecure())
//		.headers(headers -> headers.httpStrictTransportSecurity(hsts -> hsts.disable()))
				.sessionManagement(
						sessionPolicy -> sessionPolicy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				// handlers for 403 and 401
				.exceptionHandling((exception) -> exception.authenticationEntryPoint(new RestAuthenticationEntryPoint())
						.accessDeniedHandler(new CustomAccessDeinedHandler()))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/", "/error", "/api/all", "/api/auth/**", "/oauth2/**")
								.permitAll().requestMatchers("/api/v1/**").hasAuthority(UserRoleEnum.ROLE_USER.name())
								.anyRequest().authenticated())

				.oauth2Login(oauth2 -> oauth2
						.authorizationEndpoint(authEndpoint -> authEndpoint
								.authorizationRequestRepository(cookieAuthorizationRequestRepository()))
						.redirectionEndpoint(Customizer.withDefaults())
						.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.oidcUserService(customOidcUserService)
								.userService(customOAuth2UserService))
						.tokenEndpoint(tokenEndpoint -> tokenEndpoint
								.accessTokenResponseClient(authorizationCodeTokenResponseClient()))
						.successHandler(oAuth2AuthenticationSuccessHandler)
						.failureHandler(oAuth2AuthenticationFailureHandler))
				.securityContext((securityContext) -> securityContext
						.securityContextRepository(securityContextRepository))
				;

		// Add our custom Token based authentication filter
		return http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).build();

	}

	@Bean
	protected TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter();
	}

//	@Bean
//	protected SecurityContextRepository securityContextRepository() {
//		return new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository());
//	}

	/*
	 * By default, Spring OAuth2 uses
	 * HttpSessionOAuth2AuthorizationRequestRepository to save the authorization
	 * request. But, since our service is stateless, we can't save it in the
	 * session. We'll save the request in a Base64 encoded cookie instead.
	 */
	@Bean
	protected HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}

	@Bean
	protected AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
		OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
		tokenResponseHttpMessageConverter
				.setAccessTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());
		RestTemplate restTemplate = new RestTemplate(
				Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
		tokenResponseClient.setRestOperations(restTemplate);
		return tokenResponseClient;
	}
}
