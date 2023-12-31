package com.mine.security.oauth2;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OAuth2AccessTokenResponseConverterWithDefaults implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {
	
	private static final Logger log = LoggerFactory.getLogger(OAuth2AccessTokenResponseConverterWithDefaults.class);
	
    private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream
            .of(OAuth2ParameterNames.ACCESS_TOKEN, OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.EXPIRES_IN, OAuth2ParameterNames.REFRESH_TOKEN, OAuth2ParameterNames.SCOPE)
            .collect(Collectors.toSet());
 
    private OAuth2AccessToken.TokenType defaultAccessTokenType = OAuth2AccessToken.TokenType.BEARER;
 
    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
        String accessToken = (String) tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
 
        OAuth2AccessToken.TokenType accessTokenType = this.defaultAccessTokenType;
        if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase((String) tokenResponseParameters.get(OAuth2ParameterNames.TOKEN_TYPE))) {
            accessTokenType = OAuth2AccessToken.TokenType.BEARER;
        }
 
        long expiresIn = 0;
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
            try {
                expiresIn = Long.valueOf((Integer)tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN));
            } catch (Exception ex) {
            	log.error("OAuth2AccessTokenResponseConverterWithDefaults - error : {}",ex);
            	throw ex;
            }
        }
 
        Set<String> scopes = Collections.emptySet();
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
            String scope = (String) tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " ")).collect(Collectors.toSet());
        }
 
        Map<String, Object> additionalParameters = new LinkedHashMap<>();
        tokenResponseParameters.entrySet().stream().filter(e -> !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey()))
                .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));
 
        return OAuth2AccessTokenResponse.withToken(accessToken).tokenType(accessTokenType).expiresIn(expiresIn).scopes(scopes).additionalParameters(additionalParameters).build();
    }
 
    public final void setDefaultAccessTokenType(OAuth2AccessToken.TokenType defaultAccessTokenType) {
        Assert.notNull(defaultAccessTokenType, "defaultAccessTokenType cannot be null");
        this.defaultAccessTokenType = defaultAccessTokenType;
    }
}