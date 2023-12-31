package com.mine.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends AuthenticationException{

	private static final long serialVersionUID = 5485684071933561523L;

	public UserAlreadyExistAuthenticationException(String msg) {
		super(msg);
	}
	
}
