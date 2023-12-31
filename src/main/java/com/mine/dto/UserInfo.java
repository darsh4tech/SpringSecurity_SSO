package com.mine.dto;

import java.util.Set;

import lombok.Value;

@Value
public class UserInfo {

	private String userName, email;
	private Set<String> roles;

}
