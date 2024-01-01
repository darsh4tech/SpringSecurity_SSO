package com.mine.entity.auditing;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mine.dto.LocalUser;

public class AuditorAwareImpl implements AuditorAware<String> {

	private static final Logger logger = LoggerFactory.getLogger(AuditorAwareImpl.class);

	
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.info("authentication : {}",authentication);
		return Optional.ofNullable(authentication).map(auth -> {
			LocalUser localUser = (LocalUser) auth.getPrincipal();
			logger.info("localUser : {}",(LocalUser) auth.getPrincipal());
			return localUser.getEmail();
		}).or( () -> Optional.of("User_ANONYMOUS"));
	}

}
