package com.mine.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
 
	private String tokenSecret;
    private String expireTokenInSeconds;
    private String expireRefreshTokenInSeconds;

	private OAuth2 oauth2;

	public OAuth2 getOauth2() {
		return oauth2;
	}

	public void setOauth2(OAuth2 oauth2) {
		this.oauth2 = oauth2;
	}
    
    public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getExpireTokenInSeconds() {
		return expireTokenInSeconds;
	}

	public void setExpireTokenInSeconds(String expireTokenInSeconds) {
		this.expireTokenInSeconds = expireTokenInSeconds;
	}

	public String getExpireRefreshTokenInSeconds() {
		return expireRefreshTokenInSeconds;
	}

	public void setExpireRefreshTokenInSeconds(String expireRefreshTokenInSeconds) {
		this.expireRefreshTokenInSeconds = expireRefreshTokenInSeconds;
	}

	public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
 
        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }
 
        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }
    
}