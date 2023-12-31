package com.mine.security.oauth2.provider;

import java.util.Map;

import com.mine.dto.SocialType;
import com.mine.exception.OAuth2AuthenticationProcessingException;

public class OAuth2UserInfoFactory {
	
	private OAuth2UserInfoFactory() {}
	
    public static OAuth2UserInfo getOAuth2UserInfo(String socialProviderName, Map<String, Object> attributes) {
        if (socialProviderName.equalsIgnoreCase(SocialType.GOOGLE.getProvider())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (socialProviderName.equalsIgnoreCase(SocialType.FACEBOOK.getProvider())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + socialProviderName + " is not supported yet.");
        }
    }
}