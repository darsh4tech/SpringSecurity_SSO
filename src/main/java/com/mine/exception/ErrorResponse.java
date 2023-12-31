package com.mine.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

	private long timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
	private String data;
	
}
