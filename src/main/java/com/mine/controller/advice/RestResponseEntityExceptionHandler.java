package com.mine.controller.advice;

import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mine.dto.ApiResponse;
import com.mine.exception.ErrorResponse;
import com.mine.exception.OAuth2AuthenticationProcessingException;
import com.mine.utils.CookieUtils;

import jakarta.persistence.NonUniqueResultException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private CookieUtils cookieUtils;

//    @ExceptionHandler
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("400 Status Code", ex);
		final BindingResult result = ex.getBindingResult();

		String error = result.getAllErrors().stream().map(e -> {
			if (e instanceof FieldError) {
				return ((FieldError) e).getField() + " : " + e.getDefaultMessage();
			} else {
				return e.getObjectName() + " : " + e.getDefaultMessage();
			}
		}).collect(Collectors.joining(", "));
		return handleExceptionInternal(ex, new ApiResponse(false, error), new HttpHeaders(), HttpStatus.BAD_REQUEST,
				request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenralException(RuntimeException e, HttpServletRequest request) {
		logger.error("I am here : handleGenralException ");
		logger.error(e);
		return ResponseEntity.status(500).body(new ErrorResponse(System.currentTimeMillis(), 500,
				HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage(), request.getRequestURI(), request.getRemoteHost()));
	}

	@ExceptionHandler({NonUniqueResultException.class, BadRequestException.class})
	public ResponseEntity<ErrorResponse> handleGenralNonUniqueResultException(NonUniqueResultException e, HttpServletRequest request) {
		logger.error("I am here : BAD_REQUEST ");
		logger.error(e);
		return ResponseEntity.status(400).body(new ErrorResponse(System.currentTimeMillis(), 400,
				HttpStatus.BAD_REQUEST.name(), e.getMessage(), request.getRequestURI(), request.getRemoteHost()));
	}

	@ExceptionHandler(OAuth2AuthenticationProcessingException.class)
	public ResponseEntity<ErrorResponse> handleGenralOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException e, HttpServletRequest request) {
		logger.error("I am here : FORBIDDEN ");
		logger.error(e);
		return ResponseEntity.status(403).body(new ErrorResponse(System.currentTimeMillis(), 403,
				HttpStatus.FORBIDDEN.name(), e.getMessage(), request.getRequestURI(), request.getServletPath()));
	}

}