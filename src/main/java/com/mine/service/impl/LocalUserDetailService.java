package com.mine.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mine.dto.LocalUser;
import com.mine.dto.SocialType;
import com.mine.entity.AppUser;
import com.mine.exception.ResourceNotFoundException;
import com.mine.service.UserService;
import com.mine.utils.GeneralUtils;

@Service
public class LocalUserDetailService implements UserDetailsService {

	@Autowired
    private UserService userService;
 
    @Override
    @Transactional
    public LocalUser loadUserByUsername(final String email) throws UsernameNotFoundException {
    	AppUser user = userService.findUserByEmailAndSocialType(email, SocialType.LOCAL.name());
    	if (user == null)
    		throw new UsernameNotFoundException("this user " + email + " is not Exist");
    	return createLocalUser(user);
    }
 
    @Transactional
    public LocalUser loadUserById(Long id) {
    	AppUser user = userService.findUserById(id);
    	if (user == null)
    		throw new ResourceNotFoundException("AppUser", "id", id);
        return createLocalUser(user);
    }
 
    /**
     * @param user
     * @return
     */
    private LocalUser createLocalUser(AppUser user) {
        return new LocalUser(user, true, true, true, GeneralUtils.buildSimpleGrantedAuthorities(user.getRoles()));
    }

}
