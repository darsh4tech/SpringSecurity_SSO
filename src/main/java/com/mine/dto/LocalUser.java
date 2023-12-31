package com.mine.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.mine.entity.AppUser;
import com.mine.entity.UserStatus;
import com.mine.utils.GeneralUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LocalUser extends User implements OAuth2User, OidcUser, Serializable {

	private static final long serialVersionUID = -1826881673176661412L; 
	
	private AppUser appUser;
	private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private Map<String, Object> attributes;
    
	public LocalUser(AppUser appUser, final boolean accountNonExpired, final boolean credentialsNonExpired,
            final boolean accountNonLocked, final Collection<? extends GrantedAuthority> authorities) {
        this(appUser,accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, null, null);
    }
 
    public LocalUser(AppUser appUser, final boolean accountNonExpired, final boolean credentialsNonExpired,
            final boolean accountNonLocked, final Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
            OidcUserInfo userInfo) {
		super(appUser.getEmail(), appUser.getPassword(), appUser.getStatus().equals(UserStatus.ENABLED), accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);    		
        this.appUser = appUser;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }
 
    public static LocalUser create(AppUser appUser, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
    	LocalUser localUser = null;
		localUser = new LocalUser(appUser, true, true, true, GeneralUtils.buildSimpleGrantedAuthorities(appUser.getRoles()), idToken, userInfo);
        localUser.setAttributes(attributes);
        return localUser;
    }

	@Override
	public String getName() {
		return this.appUser.getUserName();
	}

	@Override
	public Map<String, Object> getClaims() {
		return this.attributes;
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return appUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet());
	}
	
}
