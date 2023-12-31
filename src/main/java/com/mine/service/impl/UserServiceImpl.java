package com.mine.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mine.dto.LocalUser;
import com.mine.dto.SignUpRequest;
import com.mine.dto.SocialType;
import com.mine.dto.UserRoleEnum;
import com.mine.entity.AppUser;
import com.mine.entity.UserStatus;
import com.mine.exception.OAuth2AuthenticationProcessingException;
import com.mine.exception.UserAlreadyExistAuthenticationException;
import com.mine.repo.UserRepository;
import com.mine.security.oauth2.provider.OAuth2UserInfo;
import com.mine.security.oauth2.provider.OAuth2UserInfoFactory;
import com.mine.service.UserService;
import com.mine.utils.GeneralUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
  
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    @Override
    @Transactional(value = "transactionManager")
    public AppUser registerNewUser(final SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException {
        
    	AppUser credentialUser = findUserByEmailAndSocialType(signUpRequest.getEmail(), SocialType.LOCAL.name());
        
    	if(credentialUser != null)
    		throw new UserAlreadyExistAuthenticationException("you have an account linked with this mail "+signUpRequest.getEmail());

        AppUser user = buildUser(signUpRequest);
        user = userRepository.save(user);
        return user;
    }
 
    private AppUser buildUser(final SignUpRequest formDTO) {
        AppUser user = new AppUser();
        user.setUserName(formDTO.getDisplayName());
        user.setEmail(formDTO.getEmail());
        user.setPassword(passwordEncoder.encode(formDTO.getPassword()));
        user.setRoles(Set.of(UserRoleEnum.ROLE_USER.name()));
    	user.setSocialType(SocialType.LOCAL);
        // TODO : disable it and send mail to enable user later
        user.setStatus(UserStatus.ENABLED);
        return user;
    }
 
    @Override
    @Transactional
    public LocalUser processOauth2Registration(String socialProviderName, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        
    	OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialProviderName, attributes);
        
    	if (!StringUtils.hasText(oAuth2UserInfo.getName())) {
            throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
        } else if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
    	
        //check email & social in case the user used the same mail in face_book & google
        AppUser user = findUserByEmailAndSocialType(oAuth2UserInfo.getEmail(), socialProviderName);

        // create new record or update existing
        if (user == null) {
        	user = registerNewSocialUser(socialProviderName, oAuth2UserInfo);
        } else {
            user = updateExistingUser(user);
        }
        return LocalUser.create(user, attributes, idToken, userInfo);
    }
 
	private AppUser registerNewSocialUser(String socialProviderName, OAuth2UserInfo oAuth2UserInfo) {
		AppUser socialUser = new AppUser();
		socialUser.setUserName(oAuth2UserInfo.getName());
		socialUser.setEmail(oAuth2UserInfo.getEmail());
		socialUser.setRoles(Set.of(UserRoleEnum.ROLE_USER.name()));
		socialUser.setSocialType(GeneralUtils.toSocialProvider(socialProviderName));
		socialUser.setSocialUserId(oAuth2UserInfo.getId());
		// put dummy value and will not affect on any flow in the code 
		// if we didn't set a any password then spring security will throw exception 
		// as this function inside [processOauth2Registration]
		// which return LocalUser which is a Spring Security User instance
		socialUser.setPassword(passwordEncoder.encode("changeit"));
        // TODO : disable it and send mail to enable user later
		socialUser.setStatus(UserStatus.ENABLED);
		socialUser = userRepository.save(socialUser);
		return socialUser;
	}

	private AppUser updateExistingUser(AppUser existingUser) {
        existingUser.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(existingUser);
    }

	@Override
	public AppUser findUserByEmailAndSocialType(String email, String socialProviderName) {
		return userRepository.findByEmailAndSocialType(email, GeneralUtils.toSocialProvider(socialProviderName)).orElse(null);
	}

	@Override
	public AppUser findUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}
    
}
