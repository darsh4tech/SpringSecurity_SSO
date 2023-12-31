package com.mine.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {

	private static final long serialVersionUID = -1307300286872895061L;

	public OAuth2AuthenticationProcessingException(String msg) {
		super(msg);
	}
	
	public OAuth2AuthenticationProcessingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
