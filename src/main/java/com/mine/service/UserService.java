package com.mine.service;

import java.util.Map;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import com.mine.dto.LocalUser;
import com.mine.dto.SignUpRequest;
import com.mine.entity.AppUser;
import com.mine.exception.UserAlreadyExistAuthenticationException;

public interface UserService {

	public AppUser registerNewUser(SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException;
	 
	AppUser findUserByEmailAndSocialType(String email, String socialProviderName);
 
	AppUser findUserById(Long id);
 
    LocalUser processOauth2Registration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo);
	
}
