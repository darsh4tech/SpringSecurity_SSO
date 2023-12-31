package com.mine.model.auditing;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

	private static final Logger logger = LoggerFactory.getLogger(AuditorAwareImpl.class);

	
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.info("authentication : {}",authentication);
		return Optional.ofNullable(authentication).map(auth -> (String) auth.getPrincipal()).or( () -> Optional.of("User_ANONYMOUS"));
	}

}
