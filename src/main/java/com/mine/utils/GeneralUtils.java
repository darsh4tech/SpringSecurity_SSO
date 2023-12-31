package com.mine.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mine.dto.LocalUser;
import com.mine.dto.SocialType;
import com.mine.dto.UserInfo;

public class GeneralUtils {

	public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }
  
	public static SocialType toSocialProvider(String providerId) {
		return Stream.of(SocialType.values()).filter(social -> social.getProvider().equals(providerId)).findFirst().get();
    }
	
    public static UserInfo buildUserInfo(LocalUser localUser) {
        List<String> roles = localUser.getAuthorities().stream().map(item -> item.getAuthority()).toList();
        return new UserInfo(localUser.getAppUser().getUserName(), localUser.getAppUser().getEmail(), new HashSet<String>(roles));
    }
	
}
