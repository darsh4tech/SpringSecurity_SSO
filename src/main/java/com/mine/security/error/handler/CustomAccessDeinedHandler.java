package com.mine.security.error.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAccessDeinedHandler implements AccessDeniedHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeinedHandler.class);
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
    	logger.error("CustomAccessDeinedHandler - Error stack : {}",accessDeniedException);
    	logger.error("Responding with access deined error. Message - {}", accessDeniedException.getMessage());
    	response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getLocalizedMessage());

	}

}
