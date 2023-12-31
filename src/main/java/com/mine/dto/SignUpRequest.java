package com.mine.dto;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest implements Serializable {

	private static final long serialVersionUID = 2640338576060291986L;

	private Long userID;

	@NotEmpty
	private String displayName;

	@NotEmpty
	@Email
	private String email;

	private SocialType socialType;
	private String socialUserId;
	
	@Size(min = 6, message = "{Size.userDto.password}")
	private String password;
	@NotEmpty
	private String matchingPassword;
	
	@Enumerated(EnumType.STRING)
    private Set<UserRoleEnum> userRoles;
	
}
